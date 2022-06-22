package org.wallentines.midnightlib.config.serialization;

import org.wallentines.midnightlib.config.ConfigRegistry;
import org.wallentines.midnightlib.config.ConfigSection;

public class ClassSerializer<T> implements Serializer<T, Object> {

    private final Class<T> clazz;
    private final ConfigRegistry registry;

    public ClassSerializer(Class<T> clazz) {
        this(clazz, ConfigRegistry.INSTANCE);
    }

    public ClassSerializer(Class<T> clazz, ConfigRegistry registry) {

        this.clazz = clazz;
        this.registry = registry;
    }

    @Override
    public Object serialize(T value) {

        ConfigSerializer<T> ser = registry.getSerializer(clazz, ConfigRegistry.Direction.SERIALIZE);
        if(ser != null) return ser.serialize(value);

        InlineSerializer<T> iSer = registry.getInlineSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
        if(iSer != null) return iSer.serialize(value);

        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Object object) {

        if(object.getClass() == clazz || clazz.isAssignableFrom(object.getClass())) return (T) object;

        if(object instanceof ConfigSection) {
            ConfigSerializer<T> ser = registry.getSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
            if (ser != null) return ser.deserialize((ConfigSection) object);
        }

        InlineSerializer<T> iSer = registry.getInlineSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
        if(iSer != null) return iSer.deserialize(object.toString());

        return null;
    }

    @Override
    public boolean canDeserialize(Object object) {

        return (object instanceof ConfigSection) && registry.canSerialize(clazz) || registry.canSerializeInline(clazz);
    }
}
