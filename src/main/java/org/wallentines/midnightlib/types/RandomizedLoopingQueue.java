package org.wallentines.midnightlib.types;

import java.util.Collection;

public class RandomizedLoopingQueue<T> extends RandomizedQueue<T> {

    private final Collection<T> options;

    public RandomizedLoopingQueue(Collection<T> options) {

        if(options.isEmpty()) throw new IllegalArgumentException("Cannot create randomized looping queue with zero options!");
        this.options = options;

    }

    @Override
    public T peek() {
        if(size() == 0) refill();
        return super.peek();
    }

    @Override
    public T poll() {
        if(size() == 0) refill();
        return super.poll();
    }

    private void refill() {

        this.addAll(options);
    }

}
