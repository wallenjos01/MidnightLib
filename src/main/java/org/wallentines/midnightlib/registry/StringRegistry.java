package org.wallentines.midnightlib.registry;


import org.wallentines.mdcfg.serializer.InlineSerializer;

/**
 * A Registry with String keys
 * @param <T> The type of data stored in the registry
 */
public class StringRegistry<T> extends RegistryBase<String ,T> {

    /**
     * Constructs a new String registry which allows duplicate values
     */
    public StringRegistry() {
        this(true, false);
    }

    /**
     * Constructs a new String registry with the given duplicate strategy
     * @param allowDuplicateValues Whether the registry should allow duplicate values
     * @param allowNullValues Whether null values can be registered
     */
    public StringRegistry(boolean allowDuplicateValues, boolean allowNullValues) {
        super(allowDuplicateValues, allowNullValues);
    }

    @Override
    public InlineSerializer<T> nameSerializer() {
        return InlineSerializer.of(this::getId, this::get);
    }

}
