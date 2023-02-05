package org.wallentines.midnightlib.config.serialization;


import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Deprecated
public class ListSerializer<T, O> implements Serializer<Collection<T>, Collection<O>> {

    private final Serializer<T, O> base;

    public ListSerializer(Serializer<T, O> base) {
        this.base = base;
    }

    @Override
    public Collection<O> serialize(Collection<T> value) {
        return value.stream().map(base::serialize).collect(Collectors.toList());
    }

    @Override
    public Collection<T> deserialize(Collection<O> object) {
        return object.stream().map(base::deserialize).collect(Collectors.toList());
    }

    @Override
    public boolean canDeserialize(Collection<O> object) {
        for(O sec : object) {
            if(!base.canDeserialize(sec)) return false;
        }
        return true;
    }

    @Override
    public <R> ConfigSerializer.Entry<Collection<T>, R> entry(String key, Function<R, Collection<T>> getter) {
        return ConfigSerializer.entry(this, key, getter);
    }

}
