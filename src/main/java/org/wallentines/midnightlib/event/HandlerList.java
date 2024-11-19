package org.wallentines.midnightlib.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class for invoking and handling events
 * @param <T> The type of event to handle
 */
public class HandlerList<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Event");

    private final PriorityQueue<WrappedHandler> handlers = new PriorityQueue<>();

    // To prevent concurrent modification exceptions, calls to register(), invoke(), or unregisterAll() will be deferred
    // if an event is currently being invoked
    private final Queue<Runnable> waiting = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean invoking = new AtomicBoolean(false);

    /**
     * Registers a new event handler with the given listener
     * @param listener The registered listener. This can be anything, but if it is garbage-collected, the handler will
     *                 be removed from the list
     * @param handler The handler to call when an event is invoked
     */
    public void register(Object listener, EventHandler<T> handler) {
        register(listener, 50, handler);
    }

    /**
     * Registers a new event handler with the given listener and priority
     * @param listener The registered listener. This can be anything, but if it is garbage-collected, the handler will
     *                 be removed from the list
     * @param priority The priority of the handler. Handlers with lower priorities are called earlier
     * @param handler The handler to call when an event is invoked
     */
    public void register(Object listener, int priority, EventHandler<T> handler) {

        run(() -> handlers.add(new WrappedHandler(listener, priority, handler)));
    }

    /**
     * Invokes an event
     * @param event The event to invoke
     */
    public void invoke(T event) {

        run(() -> {
            invoking.set(true);

            // Keep track of all handlers which have been garbage-collected
            List<WrappedHandler> toRemove = new ArrayList<>();

            for (WrappedHandler handler : handlers) {
                if (handler == null || handler.listener.get() == null) {
                    toRemove.add(handler);
                    continue;
                }
                handle(handler.handler, event);
            }

            // Remove all handlers which have been garbage-collected
            handlers.removeAll(toRemove);

            invoking.set(false);

            // Call all deferred functions
            while(!waiting.isEmpty()) {
                waiting.remove().run();
            }
        });
    }

    /**
     * Handles a given event by invoking the given event handler
     * @param handler The handler to invoke
     * @param event The event to handle
     */
    protected void handle(EventHandler<T> handler, T event) {
        try {
            handler.invoke(event);
        } catch (Throwable th) {

            LOGGER.warn("An exception was thrown while an event was being handled!", th);
        }
    }

    /**
     * Unregisters all event handlers
     */
    public void unregisterAll() {
        run(handlers::clear);
    }

    /**
     * Unregisters all event handlers with the given listener
     * @param listener The listener to lookup
     */
    public void unregisterAll(Object listener) {
        run(() -> handlers.removeIf(wrappedHandler
                -> wrappedHandler != null
                && wrappedHandler.listener.get() == listener));
    }

    private void run(Runnable run) {

        if(invoking.get()) {
            waiting.add(run);
        } else {
            run.run();
        }
    }

    private class WrappedHandler implements Comparable<WrappedHandler> {

        final WeakReference<?> listener;
        final int priority;
        final EventHandler<T> handler;

        public WrappedHandler(Object listener, int priority, EventHandler<T> handler) {
            this.listener = new WeakReference<>(listener);
            this.priority = priority;
            this.handler = handler;
        }

        @Override
        public int compareTo(WrappedHandler o) {
            return Integer.compare(priority, o.priority);
        }
    }

}
