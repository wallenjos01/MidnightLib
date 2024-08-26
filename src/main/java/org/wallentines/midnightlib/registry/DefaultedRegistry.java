package org.wallentines.midnightlib.registry;

import org.jetbrains.annotations.NotNull;
import org.wallentines.mdcfg.serializer.InlineSerializer;

import java.util.List;
import java.util.Map;

public class DefaultedRegistry<I, T> extends Registry<I, T> {

    private final T defaultValue;

    public DefaultedRegistry(T defaultValue, InlineSerializer<I> idSerializer) {
        super(idSerializer);
        this.defaultValue = defaultValue;
    }

    public DefaultedRegistry(T defaultValue, InlineSerializer<I> idSerializer, boolean allowDuplicateValues, boolean allowNullValues, boolean allowEqualValues) {
        super(idSerializer, allowDuplicateValues, allowNullValues, allowEqualValues);
        this.defaultValue = defaultValue;
    }

    protected DefaultedRegistry(T defaultValue, InlineSerializer<I> idSerializer, boolean allowDuplicateValues, boolean allowNullValues, boolean allowEqualValues, List<I> ids, List<T> values, Map<I, Integer> indexById, Map<T, Integer> indexByValue) {
        super(idSerializer, allowDuplicateValues, allowNullValues, allowEqualValues, ids, values, indexById, indexByValue);
        this.defaultValue = defaultValue;
    }

    @Override
    public T get(@NotNull I id) {

        if(!super.hasKey(id)) {
            return defaultValue;
        }

        return super.get(id);
    }
}
