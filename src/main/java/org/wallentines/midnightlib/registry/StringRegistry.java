package org.wallentines.midnightlib.registry;


import org.wallentines.mdcfg.serializer.InlineSerializer;

public class StringRegistry<T> extends RegistryBase<String ,T> {

    public StringRegistry() {
        super(true);
    }

    public StringRegistry(boolean allowDuplicateValues) {
        super(allowDuplicateValues);
    }

    @Override
    public InlineSerializer<T> nameSerializer() {
        return InlineSerializer.of(this::getId, this::get);
    }

}
