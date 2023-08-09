package org.wallentines.midnightlib.event;

import java.util.HashMap;

/**
 * A static class for invoking global events and registering handlers for them
 */
public final class Event {

    private static final HashMap<Class<?>, HandlerList<?>> EVENTS = new HashMap<>();

    /**
     * Registers an event handler for events of the given class
     * @param ev The type of event to listen to
     * @param listener The object listening to the event. If this is garbage-collected, the handler will be removed
     * @param handler The handler to run when the event is fired
     * @param <T> The type of event to listen to
     */
    public static <T> void register(Class<T> ev, Object listener, EventHandler<T> handler) {

        register(ev, listener, 50, handler);
    }

    /**
     * Registers an event handler for events of the given class, with the given priority
     * @param ev The type of event to listen to
     * @param listener The object listening to the event. If this is garbage-collected, the handler will be removed
     * @param priority The priority of the handler. Handlers with lower priorities will be called first
     * @param handler The handler to run when the event is fired
     * @param <T> The type of event to listen to
     */
    @SuppressWarnings("unchecked")
    public static <T> void register(Class<T> ev, Object listener, int priority, EventHandler<T> handler) {

        if(listener == null || handler == null) return;

        HandlerList<T> list = (HandlerList<T>) EVENTS.computeIfAbsent(ev, k -> new HandlerList<T>());
        list.register(listener, priority, handler);

    }

    /**
     * Invokes a global event
     * @param event The event to invoke
     * @param <T> The type of event to invoke
     */
    @SuppressWarnings("unchecked")
    public static <T> void invoke(T event) {

        if(event == null) return;

        HandlerList<T> handlers = (HandlerList<T>) EVENTS.get(event.getClass());
        if(handlers == null) return;

        handlers.invoke(event);

    }

    /**
     * Unregisters all event handlers with the given listener
     * @param listener The listener to lookup
     */
    public static void unregisterAll(Object listener) {

        for(HandlerList<?> l : EVENTS.values()) {
            l.unregisterAll(listener);
        }
    }

    /**
     * Unregisters all events with the given type
     * @param event The type of event to unregister
     */
    public static void unregisterAll(Class<?> event) {

        if(EVENTS.containsKey(event)) {
            EVENTS.get(event).unregisterAll();
        }
    }

}
