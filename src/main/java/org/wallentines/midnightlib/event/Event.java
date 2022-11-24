package org.wallentines.midnightlib.event;

import java.util.HashMap;

public class Event {

    private static final HashMap<Class<? extends Event>, HandlerList<? extends Event>> events = new HashMap<>();

    public static <T extends Event> void register(Class<T> ev, Object listener, EventHandler<T> handler) {

        register(ev, listener, 50, handler);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void register(Class<T> ev, Object listener, int priority, EventHandler<T> handler) {

        if(listener == null || handler == null) return;

        events.computeIfAbsent(ev, k -> new HandlerList<T>());

        HandlerList<T> list = (HandlerList<T>) events.get(ev);
        list.register(listener, priority, handler);

    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> void invoke(T event) {

        if(event == null) return;

        HandlerList<T> handlers = (HandlerList<T>) events.get(event.getClass());
        if(handlers == null) return;

        handlers.invoke(event);

    }

    public static void unregisterAll(Object o) {

        for(HandlerList<?> l : events.values()) {
            l.unregisterAll(o);
        }
    }

    public static void unregisterAll(Class<? extends Event> event) {

        if(events.containsKey(event)) {
            events.get(event).unregisterAll();
        }
    }

    protected void cancel() {

        HandlerList<?> handlers = events.get(getClass());
        if(handlers == null) return;

        handlers.cancel();

    }

}
