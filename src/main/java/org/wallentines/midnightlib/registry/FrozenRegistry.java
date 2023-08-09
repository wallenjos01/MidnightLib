package org.wallentines.midnightlib.registry;

import org.wallentines.mdcfg.serializer.InlineSerializer;

import java.util.List;
import java.util.Map;

public class FrozenRegistry<I, T> extends RegistryBase<I, T> {

    private final InlineSerializer<T> nameSerializer;

    public FrozenRegistry(RegistryBase<I, T> registry, InlineSerializer<T> nameSerializer) {
        super(registry.allowDuplicateValues, List.copyOf(registry.ids), List.copyOf(registry.values), Map.copyOf(registry.indexById), Map.copyOf(registry.indexByValue));
        this.nameSerializer = nameSerializer;
    }

    @Override
    public T register(I id, T value) throws IllegalArgumentException {
        throw new IllegalStateException("Registry is frozen!");
    }

    @Override
    public void clear() {
        throw new IllegalStateException("Registry is frozen!");
    }

    @Override
    public T remove(I id) {
        throw new IllegalStateException("Registry is frozen!");
    }

    @Override
    public T removeValue(T value) {
        throw new IllegalStateException("Registry is frozen!");
    }

    @Override
    public T removeAtIndex(int index) {
        throw new IllegalStateException("Registry is frozen!");
    }

    @Override
    public InlineSerializer<T> nameSerializer() {
        return nameSerializer;
    }
}
