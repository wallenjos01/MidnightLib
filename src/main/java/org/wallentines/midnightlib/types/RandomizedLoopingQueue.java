package org.wallentines.midnightlib.types;

import java.util.Collection;
import java.util.Random;

/**
 * A randomized queue which will always refill itself from a given Collection of objects when it becomes empty
 * @param <T> The type of objects in the queue
 */
public class RandomizedLoopingQueue<T> extends RandomizedQueue<T> {

    private final Collection<T> options;

    /**
     * Constructs a new randomized looping queue with the given options and the built-in Random object
     * @param options The options which will be in the queue at first, and will be used to refill the queue if ever empties
     * @throws IllegalArgumentException iff the collection is empty
     */
    public RandomizedLoopingQueue(Collection<T> options) {

        if(options.isEmpty()) throw new IllegalArgumentException("Cannot create randomized looping queue with zero options!");
        this.options = options;

    }

    /**
     * Constructs a new randomized looping queue with the given options and the given Random object
     * @param options The options which will be in the queue at first, and will be used to refill the queue if ever empties
     * @param rand The Random object to use for randomization
     * @throws IllegalArgumentException iff the collection is empty
     */
    public RandomizedLoopingQueue(Collection<T> options, Random rand) {

        super(rand);

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
