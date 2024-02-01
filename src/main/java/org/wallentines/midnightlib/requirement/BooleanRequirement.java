package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class BooleanRequirement<T> implements Predicate<T> {

    public static <T> Serializer<BooleanRequirement<T>> serializer(Function<T, Boolean> getter) {
        return serializer(req -> req.value, value -> new BooleanRequirement<>(getter, value));
    }

    public static <T, R> Serializer<R> serializer(Function<R,Boolean> backGetter, Function<Boolean, R> constructor) {
        return Serializer.BOOLEAN.map(backGetter, constructor);
    }

    private final Function<T, Boolean> getter;
    private final Boolean value;

    public BooleanRequirement(Function<T, Boolean> getter, Boolean value) {
        this.getter = getter;
        this.value = value;
    }

    @Override
    public boolean test(T data) {
        return Objects.equals(getter.apply(data), value);
    }
}
