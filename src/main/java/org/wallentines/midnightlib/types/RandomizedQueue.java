package org.wallentines.midnightlib.types;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class RandomizedQueue<T> extends AbstractQueue<T> {

    private static final Random RANDOM = new Random();
    private final LinkedList<T> internal = new LinkedList<>();

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

        if(internal.size() == 0) {
            internal.add(t);
            return true;
        }

        int index = RANDOM.nextInt(internal.size());
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
