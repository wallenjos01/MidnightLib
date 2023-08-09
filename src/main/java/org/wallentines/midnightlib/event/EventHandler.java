package org.wallentines.midnightlib.event;

/**
 * A functional interface for handling events
 * @param <T> The type of event to handle
 */
public interface EventHandler<T> {

    /**
     * Handles an event
     * @param event The event data
     */
    void invoke(T event);

}
