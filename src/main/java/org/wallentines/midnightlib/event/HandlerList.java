package org.wallentines.midnightlib.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class HandlerList<T extends Event> {

    private static final Logger LOGGER = LogManager.getLogger("Event");

    private final PriorityQueue<WrappedHandler> handlers = new PriorityQueue<>();

    private final List<Runnable> waiting = new ArrayList<>();

    private boolean cancelled = false;
    private boolean invoking = false;

    public void register(Object listener, EventHandler<T> handler) {
        register(listener, 50, handler);
    }

    public void register(Object listener, int priority, EventHandler<T> handler) {

        WrappedHandler hand = new WrappedHandler();
        hand.handler = handler;
        hand.priority = priority;
        hand.listener = new WeakReference<>(listener);

        run(() -> handlers.add(hand));
    }

    public void invoke(T event) {

        if(invoking) return;
        invoking = true;

        for(WrappedHandler handler : handlers) {

            if(handler.listener.get() == null) return;

            try {
                handler.handler.invoke(event);
            } catch (Throwable th) {

                LOGGER.warn("An exception was thrown while an event was being handled!");
                th.printStackTrace();
            }

            if(cancelled) break;
        }

        cancelled = false;
        invoking = false;

        waiting.forEach(Runnable::run);
        waiting.clear();
    }

    public void cancel() {
        cancelled = true;
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

        WeakReference<?> listener;
        int priority;
        EventHandler<T> handler;

        @Override
        public int compareTo(HandlerList<T>.WrappedHandler o) {
            return priority - o.priority;
        }
    }

}
