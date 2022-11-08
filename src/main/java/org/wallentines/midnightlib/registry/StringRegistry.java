package org.wallentines.midnightlib.registry;

import org.wallentines.midnightlib.config.serialization.InlineSerializer;

public class StringRegistry<T> extends RegistryBase<String ,T> {

    @Override
    public InlineSerializer<T> nameSerializer() {
        return InlineSerializer.of(this::getId, this::get);
    }

}
