package org.wallentines.midnightlib.types;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A Queue which randomizes objects as they enter
 * @param <T> The type of objects in the queue
 */
public class RandomizedQueue<T> extends AbstractQueue<T> {

    protected static final Random RANDOM = new Random();
    private final Random rand;
    private final List<T> internal = new ArrayList<>();

    /**
     * Constructs a new queue using the built-in Random object
     */
    public RandomizedQueue() {
        this.rand = RANDOM;
    }

    /**
     * Constructs a new queue using the given Random object
     * @param rand The random object to use for randomization
     */
    public RandomizedQueue(Random rand) {
        this.rand = rand;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return internal.iterator();
    }

    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean offer(T t) {

        if(internal.isEmpty()) {
            internal.add(t);
            return true;
        }

        int index = rand.nextInt(internal.size() + 1);
        internal.add(index, t);

        return true;
    }

    @Override
    public T poll() {
        return internal.remove(0);
    }

    @Override
    public T peek() {
        return internal.get(0);
    }


}
