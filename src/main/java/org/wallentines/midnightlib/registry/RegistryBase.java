package org.wallentines.midnightlib.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.InlineSerializer;

import java.util.*;

/**
 * An abstract data type for storing values associated with keys. Dy default, values cannot be overwritten
 * @param <I> The key type
 * @param <T> The value type
 */
public abstract class RegistryBase<I, T> implements Iterable<T> {

    protected final List<I> ids;
    protected final List<T> values;

    protected final Map<I, Integer> indexById;
    protected final Map<T, Integer> indexByValue;
    protected int size;
    protected final boolean allowDuplicateValues;
    protected final boolean allowNullValues;
    protected final boolean allowEqualValues;

    protected RegistryBase(boolean allowDuplicateValues, boolean allowNullValues, boolean allowEqualValues) {
        this(allowDuplicateValues, allowNullValues, allowEqualValues, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>());
    }

    protected RegistryBase(boolean allowDuplicateValues, boolean allowNullValues, boolean allowEqualValues, List<I> ids, List<T> values, Map<I, Integer> indexById, Map<T, Integer> indexByValue) {
        this.allowDuplicateValues = allowDuplicateValues;
        this.allowNullValues = allowNullValues;
        this.allowEqualValues = allowEqualValues;
        this.ids = ids;
        this.values = values;
        this.indexById = indexById;
        this.indexByValue = indexByValue;
    }

    /**
     * Attempts to register a given value to a given ID
     * @param id The ID of the value to register
     * @param value The value to register
     * @return The registered value
     * @throws IllegalArgumentException If there is already an ID with the same name, or the value is a duplicate
     */
    public T register(I id, T value) throws IllegalArgumentException {

        if(value == null && !allowNullValues) {
            throw new IllegalArgumentException("This registry cannot accept null values!");
        }
        if(ids.contains(id)) {
            throw new IllegalArgumentException("Attempt to register value with duplicate ID!");
        }
        if(!allowEqualValues && indexByValue.containsKey(value)) {
            throw new IllegalArgumentException("Attempt to register a value equal to an existing value! (" + value + ")");
        }
        if(!allowDuplicateValues && indexOf(value) != null) {
            throw new IllegalArgumentException("Attempt to register value twice! (" + value + ")");
        }

        ids.add(id);
        values.add(value);

        indexById.put(id, size);
        indexByValue.put(value, size);

        size++;

        return value;
    }

    /**
     * Gets the value associated with the given ID
     * @param id The ID to lookup
     * @return A registered value, or null if not found
     */
    @Nullable
    public T get(@NotNull I id) {

        Integer index = indexById.get(id);

        if(index == null || index < 0) {
            return null;
        }

        return values.get(index);
    }

    /**
     * Gets the ID of the given registered value
     * @param value The value to lookup
     * @return The value's ID, or null if the value is unregistered
     */
    @Nullable
    public I getId(@NotNull T value) {

        Integer index = indexOf(value);

        if(index == null || index < 0) {
            return null;
        }

        return ids.get(index);
    }

    /**
     * Gets the index into the registry of the given registered value
     * @param value The value to lookup
     * @return The index of the value, or null if the value is unregistered
     */
    @Nullable
    public Integer indexOf(T value) {

        Integer index = indexByValue.get(value);
        if(index == null) return null;

        if(values.get(index) != value) {
            // Traverse the registry
            for(int i = 0 ; i < index ; i++) {
                if(values.get(i) == value) return i;
            }
            return null;
        }

        return index;
    }

    /**
     * Determines if there is a registered value with the given ID
     * @param id The id to lookup
     * @return Whether there is a value registered to that ID
     */
    public boolean hasKey(I id) {

        return indexById.containsKey(id);
    }

    /**
     * Gets the value at a particular index
     * @param index The index into the registry
     * @return The value at the given index
     * @throws IndexOutOfBoundsException If the index is less than zero or greater than the largest index in the registry
     */
    public T valueAtIndex(int index) throws IndexOutOfBoundsException {

        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for registry of size " + size + "!");
        }

        return values.get(index);
    }

    /**
     * Gets the ID of the registered value at the given index
     * @param index The index into the registry
     * @return The ID of the value at the index
     * @throws IndexOutOfBoundsException If the index is less than zero or greater than the largest index in the registry
     */
    public I idAtIndex(int index) throws IndexOutOfBoundsException {

        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for registry of size " + size + "!");
        }

        return ids.get(index);
    }

    /**
     * Clears all values from the registry
     */
    public void clear() {

        values.clear();
        ids.clear();

        indexById.clear();
        indexByValue.clear();

        size = 0;
    }

    /**
     * Removes the registered value with the given ID from the registry.
     * <br/>
     * <br/>
     * WARNING: This is not recommended for most use cases, as it will cause the registry index to be rebuilt.
     * @param id The ID to lookup
     * @return The value which used to be associated with the given ID
     * @throws IllegalArgumentException If there is no object with the given ID
     */
    public T remove(I id) {

        Integer index = indexById.get(id);

        if(index == null || index < 0) {
            throw new IllegalArgumentException("Attempt to remove item with unregistered ID!");
        }

        return removeAtIndex(index);
    }

    /**
     * Removes the given registered value from the registry
     * <br/>
     * <br/>
     * WARNING: This is not recommended for most use cases, as it will cause the registry index to be rebuilt.
     * @param value The value to lookup
     * @return The value which used to be in the registry
     * @throws IllegalArgumentException If the given value is not registered
     */
    public T removeValue(T value) {

        Integer index = indexByValue.get(value);

        if(index == null || index < 0) {
            throw new IllegalArgumentException("Attempt to remove unregistered item!");
        }

        return removeAtIndex(index);
    }

    /**
     * Removes the registered value at the given index from the registry
     * <br/>
     * <br/>
     * WARNING: This is not recommended for most use cases, as it will cause the registry index to be rebuilt.
     * @param index The index into the registry
     * @return The value which used to be at the given index
     */
    public T removeAtIndex(int index) throws IndexOutOfBoundsException {

        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for registry of size " + size + "!");
        }

        T out = values.remove(index);
        I id = ids.remove(index);
        indexById.remove(id);
        indexByValue.remove(out);

        size--;

        for(int i = index ; i < size ; i++) {
            indexById.put(ids.get(i), i);
            indexByValue.put(values.get(i), i);
        }

        return out;
    }

    /**
     * Gets the number of registered values in the registry
     * @return The size of the registry
     */
    public int getSize() {
        return size;
    }

    /**
     * Determines if the given value is registered
     * @param value The value to lookup
     * @return Whether the value is registered
     */
    public boolean isRegistered(T value) {
        return indexByValue.containsKey(value);
    }

    /**
     * Determines if the given ID is in the registry
     * @param id The ID to lookup
     * @return Whether the ID is in the registry
     */
    public boolean contains(I id) {
        return indexById.containsKey(id);
    }

    /**
     * Gets a list of IDs of registered values
     * @return A list of registered value IDs
     */
    public Collection<I> getIds() {
        return ids;
    }

    /**
     * Creates a Serializer for getting registered values from Strings
     * @return A new Serializer
     */
    public abstract InlineSerializer<T> nameSerializer();

    /**
     * Creates an immutable registry which contains all keys and values of this registry, but cannot be modified
     * @return A frozen registry
     */
    public FrozenRegistry<I, T> freeze() {
        return new FrozenRegistry<>(this, nameSerializer());
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {
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
