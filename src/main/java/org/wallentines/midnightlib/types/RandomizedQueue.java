package org.wallentines.midnightlib.types;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * A Queue which randomizes objects as they enter
 * @param <T> The type of objects in the queue
 */
public class RandomizedQueue<T> extends AbstractQueue<T> {

    protected static final Random RANDOM = new Random();
    private final Random rand;
    private final LinkedList<T> internal = new LinkedList<>();

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
    public Iterator<T> iterator() {
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

        int index = rand.nextInt(internal.size());
        internal.add(index, t);

        return true;
    }

    @Override
    public T poll() {
        return internal.removeFirst();
    }

    @Override
    public T peek() {
        return internal.getFirst();
    }


}
