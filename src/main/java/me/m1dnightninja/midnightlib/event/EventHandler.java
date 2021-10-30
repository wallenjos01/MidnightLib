package me.m1dnightninja.midnightlib.event;

public interface EventHandler<T> {

    void invoke(T event);

}
