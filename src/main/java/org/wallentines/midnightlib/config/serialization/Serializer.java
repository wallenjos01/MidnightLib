package org.wallentines.midnightlib.config.serialization;

public interface Serializer<T, O>  {

    O serialize(T value);

    T deserialize(O object);

    boolean canDeserialize(O object);

}
