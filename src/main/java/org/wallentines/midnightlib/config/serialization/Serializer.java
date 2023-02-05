package org.wallentines.midnightlib.config.serialization;

import java.util.function.Function;

@Deprecated
public interface Serializer<T, O>  {

    O serialize(T value);

    T deserialize(O object);

    boolean canDeserialize(O object);

    default ListSerializer<T, O> listOf() {
        return new ListSerializer<>(this);
    }
    default MapSerializer<String, T, O> mapOf() {
        return new MapSerializer<>(InlineSerializer.RAW, this);
    }
    default <K> MapSerializer<K, T, O> mapOf(Serializer<K, String> keySerializer) {
        return new MapSerializer<>(keySerializer, this);
    }

    <R> ConfigSerializer.Entry<T, R> entry(String key, Function<R, T> getter);

}
