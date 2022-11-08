package org.wallentines.midnightlib.registry;

import org.wallentines.midnightlib.config.ConfigRegistry;
import org.wallentines.midnightlib.config.serialization.InlineSerializer;

public class Registry<T> extends RegistryBase<Identifier, T>{

    @Override
    public InlineSerializer<T> nameSerializer() {
        return nameSerializer(ConfigRegistry.INSTANCE.getIdSerializer());
    }

    public InlineSerializer<T> nameSerializer(Identifier.Serializer idSerializer) {
        return InlineSerializer.of(val -> getId(val).toString(), id -> get(idSerializer.deserialize(id)));
    }

}
