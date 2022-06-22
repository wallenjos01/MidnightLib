package org.wallentines.midnightlib.config.serialization;

public class PrimitiveSerializers {

    public static final InlineSerializer<String> STRING = InlineSerializer.RAW;

    public static final NumberSerializer<Byte> BYTE = new NumberSerializer<>(Number::byteValue);
    public static final NumberSerializer<Short> SHORT = new NumberSerializer<>(Number::shortValue);
    public static final NumberSerializer<Integer> INT = new NumberSerializer<>(Number::intValue);
    public static final NumberSerializer<Long> LONG = new NumberSerializer<>(Number::longValue);
    public static final NumberSerializer<Float> FLOAT = new NumberSerializer<>(Number::floatValue);
    public static final NumberSerializer<Double> DOUBLE = new NumberSerializer<>(Number::doubleValue);

    public static final ClassSerializer<Boolean> BOOLEAN = new ClassSerializer<>(Boolean.class);

    private static class NumberSerializer<T> implements Serializer<T, Object> {

        Functions.Function1<Number, T> serializer;

        public NumberSerializer(Functions.Function1<Number, T> serializer) {
            this.serializer = serializer;
        }

        @Override
        public Object serialize(T value) {
            return value;
        }

        @Override
        public T deserialize(Object object) {
            if(!(object instanceof Number)) return null;
            return serializer.create((Number) object);
        }

        @Override
        public boolean canDeserialize(Object object) {
            return object instanceof Number;
        }
    }

}
