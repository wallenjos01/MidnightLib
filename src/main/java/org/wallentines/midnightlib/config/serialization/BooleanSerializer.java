package org.wallentines.midnightlib.config.serialization;

import java.util.Objects;
import java.util.function.Function;

public class BooleanSerializer implements Serializer<Boolean, Object> {

    private final BooleanSerializerType type;

    public BooleanSerializer(BooleanSerializerType type) {
        this.type = type;
    }

    @Override
    public Object serialize(Boolean value) {
        return type.serialize.apply(value);
    }

    @Override
    public Boolean deserialize(Object object) {

        if(object instanceof Boolean) {
            return (Boolean) object;
        }
        if(object instanceof String) {
            return ((String) object).equalsIgnoreCase("true") || ((String) object).equalsIgnoreCase("yes");
        }
        if(object instanceof Number) {
            return ((Number) object).intValue() != 0;
        }

        return false;
    }

    @Override
    public boolean canDeserialize(Object object) {
        return object instanceof Boolean || object instanceof String || object instanceof Number;
    }

    @Override
    public <R> ConfigSerializer.Entry<Boolean, R> entry(String key, Function<R, Boolean> getter) {
        return ConfigSerializer.entry(this, key, getter);
    }


    public static final BooleanSerializer RAW = new BooleanSerializer(BooleanSerializerType.BOOLEAN);
    public static final BooleanSerializer STRING = new BooleanSerializer(BooleanSerializerType.STRING);
    public static final BooleanSerializer NUMBER = new BooleanSerializer(BooleanSerializerType.NUMBER);

    public enum BooleanSerializerType {
        BOOLEAN(bool -> bool),
        STRING(Objects::toString),
        NUMBER(bool -> bool ? 1 : 0);

        final Function<Boolean, Object> serialize;

        BooleanSerializerType(Function<Boolean, Object> serialize) {
            this.serialize = serialize;
        }
    }
}
