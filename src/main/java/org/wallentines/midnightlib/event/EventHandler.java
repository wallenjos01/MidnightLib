package org.wallentines.midnightlib.event;

public interface EventHandler<T> {

    void invoke(T event);

}
