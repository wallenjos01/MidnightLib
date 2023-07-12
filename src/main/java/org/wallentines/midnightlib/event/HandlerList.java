package org.wallentines.midnightlib.event;

import org.wallentines.midnightlib.types.SortedCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HandlerList<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Event");

    private final SortedCollection<WrappedHandler> handlers = new SortedCollection<>();

    private final List<Runnable> waiting = new ArrayList<>();

    private boolean invoking = false;

    public void register(Object listener, EventHandler<T> handler) {
        register(listener, 50, handler);
    }

    public void register(Object listener, int priority, EventHandler<T> handler) {

        run(() -> handlers.add(new WrappedHandler(listener, priority, handler)));
    }

    public void invoke(T event) {

        if(invoking) return;
        invoking = true;

        for (WrappedHandler handler : handlers) {

            if (handler.listener.get() == null) return;
            handle(handler.handler, event);
        }

        invoking = false;

        waiting.forEach(Runnable::run);
        waiting.clear();
    }

    protected void handle(EventHandler<T> handler, T event) {
        try {
            handler.invoke(event);
        } catch (Throwable th) {

            LOGGER.warn("An exception was thrown while an event was being handled!");
            th.printStackTrace();
        }
    }

    public void unregisterAll() {
        handlers.clear();
    }

    public void unregisterAll(Object o) {
        run(() -> handlers.removeIf(wrappedHandler -> wrappedHandler.listener.get() == o));
    }

    private void run(Runnable run) {

        if(invoking) {
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
