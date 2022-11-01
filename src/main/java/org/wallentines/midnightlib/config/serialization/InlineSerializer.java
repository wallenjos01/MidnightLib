package org.wallentines.midnightlib.config.serialization;

import org.wallentines.midnightlib.config.ConfigSection;

import java.util.function.Function;

public interface InlineSerializer<T> extends Serializer<T, String> {

    T deserialize(String s);

    String serialize(T object);

    default boolean canDeserialize(String s) {
        T val;
        try {
            val = deserialize(s);
        } catch (Exception ex) {
            return false;
        }
        return val != null;
    }

    @Deprecated
    default Serializer<T, Object> toRaw() {

        return new Serializer<>() {
            @Override
            public Object serialize(T value) {
                return value == null ? null : InlineSerializer.this.serialize(value);
            }

            @Override
            public T deserialize(Object object) {
                return InlineSerializer.this.deserialize(object.toString());
            }

            @Override
            public boolean canDeserialize(Object object) {
                return InlineSerializer.this.canDeserialize(object.toString());
            }

            @Override
            public <R> ConfigSerializer.Entry<T, R> entry(String key, Function<R, T> getter) {
                return ConfigSerializer.entry(this, key, getter);
            }
        };
    }


    static <T> InlineSerializer<T> of(Function<T, String> serialize, Function<String, T> deserialize) {
        return new InlineSerializer<T>() {
            @Override
            public T deserialize(String s) {
                return deserialize.apply(s);
            }

            @Override
            public String serialize(T object) {
                return serialize.apply(object);
            }
        };
    }

    static <T> InlineSerializer<T> of(Function<T, String> serialize, Function<String, T> deserialize, Function<String, Boolean> canDeserialize) {
        return new InlineSerializer<T>() {
            @Override
            public T deserialize(String s) {
                return deserialize.apply(s);
            }

            @Override
            public String serialize(T object) {
                return serialize.apply(object);
            }

            @Override
            public boolean canDeserialize(String s) {
                return canDeserialize.apply(s);
            }
        };
    }

    InlineSerializer<String> RAW = new InlineSerializer<>() {
        @Override
        public String deserialize(String s) {
            return s;
        }

        @Override
        public String serialize(String object) {
            return object;
        }
    };

    @Override
    default <R> ConfigSerializer.Entry<T, R> entry(String key, Function<R, T> getter) {
        return ConfigSerializer.entry(this, key, getter);
    }

}

