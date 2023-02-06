package org.wallentines.midnightlib.types;

import java.util.*;

public class SortedCollection<T extends Comparable<T>> implements Collection<T> {

    private final List<T> internal = new ArrayList<>();

    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internal.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return internal.iterator();
    }

    @Override
    public Object[] toArray() {
        return internal.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return internal.toArray(a);
    }

    @Override
    public boolean add(T t) {

        for(int i = 0 ; i < internal.size() ; i++) {
            T other = internal.get(i);
            if(t.compareTo(other) <= 0) {
                internal.add(i, t);
                return true;
            }
        }

        return internal.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return internal.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(internal).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return internal.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internal.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return internal.retainAll(c);
    }

    @Override
    public void clear() {
        internal.clear();
    }
}
