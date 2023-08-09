package org.wallentines.midnightlib.event;

import java.util.HashMap;

public class Event {

    private static final HashMap<Class<?>, HandlerList<?>> EVENTS = new HashMap<>();

    public static <T> void register(Class<T> ev, Object listener, EventHandler<T> handler) {

        register(ev, listener, 50, handler);
    }

    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> ev, Object listener, int priority, EventHandler<T> handler) {

        if(listener == null || handler == null) return;

        HandlerList<T> list = (HandlerList<T>) EVENTS.computeIfAbsent(ev, k -> new HandlerList<T>());
        list.register(listener, priority, handler);

    }

    @SuppressWarnings("unchecked")
    public static <T> void invoke(T event) {

        if(event == null) return;

        HandlerList<T> handlers = (HandlerList<T>) EVENTS.get(event.getClass());
        if(handlers == null) return;

        handlers.invoke(event);

    }

    public static void unregisterAll(Object o) {

        for(HandlerList<?> l : EVENTS.values()) {
            l.unregisterAll(o);
        }
    }

    public static void unregisterAll(Class<?> event) {

        if(EVENTS.containsKey(event)) {
            EVENTS.get(event).unregisterAll();
        }
    }

}
