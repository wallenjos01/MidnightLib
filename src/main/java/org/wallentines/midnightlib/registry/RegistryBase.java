package org.wallentines.midnightlib.registry;

import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.config.serialization.InlineSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public abstract class RegistryBase<I, T> implements Iterable<T> {

    private final ArrayList<I> ids = new ArrayList<>();
    private final ArrayList<T> values = new ArrayList<>();

    private final HashMap<I, Integer> indexById = new HashMap<>();
    private final HashMap<T, Integer> indexByValue = new HashMap<>();
    private int size;


    public T register(I id, T value) {

        if(ids.contains(id)) {
            throw new IllegalArgumentException("Attempt to register item with duplicate ID!");
        }
        if(values.contains(value)) {
            throw new IllegalArgumentException("Attempt to register item twice!");
        }

        ids.add(id);
        values.add(value);

        indexById.put(id, size);
        indexByValue.put(value, size);

        size++;

        return value;
    }

    public T get(I id) {

        if(id == null) {
            throw new IllegalArgumentException("ID must not be null!");
        }

        Integer index = indexById.get(id);

        if(index == null || index < 0) {
            return null;
        }

        return values.get(index);
    }

    public I getId(T value) {

        if(value == null) {
            throw new IllegalArgumentException("Value must not be null!");
        }

        Integer index = indexByValue.get(value);

        if(index == null || index < 0) {
            return null;
        }

        return ids.get(index);
    }

    public T valueAtIndex(int index) {

        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for registry of size " + size + "!");
        }

        return values.get(index);
    }

    public I idAtIndex(int index) {

        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for registry of size " + size + "!");
        }

        return ids.get(index);
    }

    public void clear() {

        values.clear();
        ids.clear();

        indexById.clear();
        indexByValue.clear();

        size = 0;
    }

    public T remove(T value) {

        Integer index = indexByValue.get(value);

        if(index == null || index < 0) {
            throw new IllegalStateException("Attempt to remove unregistered item!");
        }

        return removeAtIndex(index);
    }

    public T removeById(I id) {

        Integer index = indexById.get(id);

        if(index == null || index < 0) {
            throw new IllegalStateException("Attempt to remove item with unregistered ID!");
        }

        return removeAtIndex(index);
    }

    public T removeAtIndex(int index) {

        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for registry of size " + size + "!");
        }

        T out = values.remove(index);
        ids.remove(index);

        size--;

        for(int i = index ; i < size ; i++) {
            indexById.put(ids.get(i), i);
            indexByValue.put(values.get(i), i);
        }

        return out;
    }

    public int getSize() {
        return size;
    }

    public boolean isRegistered(T value) {
        return indexByValue.containsKey(value);
    }

    public boolean contains(I id) {
        return indexById.containsKey(id);
    }

    public Collection<I> getIds() {
        return indexById.keySet();
    }

    public abstract InlineSerializer<T> nameSerializer();

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                T out = valueAtIndex(index);
                index++;
                return out;
            }
        };
    }



}
