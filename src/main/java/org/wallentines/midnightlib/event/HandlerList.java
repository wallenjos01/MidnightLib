package org.wallentines.midnightlib.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * A class for invoking and handling events
 * @param <T> The type of event to handle
 */
public class HandlerList<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Event");

    protected final PriorityBlockingQueue<WrappedHandler> handlers = new PriorityBlockingQueue<>();

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

        handlers.add(new WrappedHandler(listener, priority, handler));
    }

    /**
     * Invokes an event
     * @param event The event to invoke
     */
    public void invoke(T event) {

        clearExpiredHandlers();
        for(WrappedHandler handler : handlers) {
            handle(handler.handler, event);
        }
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
        handlers.clear();
    }

    /**
     * Unregisters all event handlers with the given listener
     * @param listener The listener to lookup
     */
    public void unregisterAll(Object listener) {
        handlers.removeIf(wrappedHandler
                -> wrappedHandler != null
                && wrappedHandler.listener.get() == listener);
    }

    /**
     * Removes all handlers which have been garbage-collected
     */
    protected void clearExpiredHandlers() {
        handlers.removeIf(h -> h == null || h.listener.get() == null);
    }

    protected class WrappedHandler implements Comparable<WrappedHandler> {

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
