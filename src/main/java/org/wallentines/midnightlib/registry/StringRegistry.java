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
        this(false, false, true);
    }

    /**
     * Constructs a new String registry with the given duplicate strategy
     * @param allowDuplicateValues Whether the registry should allow duplicate values. Defaults to false
     * @param allowNullValues Whether null values can be registered. Defaults to false
     * @param allowEqualValues Whether two equal objects can be registered. Defaults to false
     */
    public StringRegistry(boolean allowDuplicateValues, boolean allowNullValues, boolean allowEqualValues) {
        super(allowDuplicateValues, allowNullValues, allowEqualValues);
    }

    @Override
    public InlineSerializer<T> nameSerializer() {
        return InlineSerializer.of(this::getId, this::get);
    }

}
