package org.wallentines.midnightlib.config.serialization;

import java.util.function.Function;

public interface InlineSerializer<T> {

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

}

