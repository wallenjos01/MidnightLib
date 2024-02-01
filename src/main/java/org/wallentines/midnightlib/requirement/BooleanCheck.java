package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class BooleanCheck<T> implements Predicate<T> {

    public static <T> Serializer<BooleanCheck<T>> serializer(Function<T, Boolean> getter) {
        return serializer(req -> req.value, value -> new BooleanCheck<>(getter, value));
    }

    public static <T, R> Serializer<R> serializer(Function<R,Boolean> backGetter, Function<Boolean, R> constructor) {
        return Serializer.BOOLEAN.fieldOf("value").map(backGetter, constructor);
    }

    private final Function<T, Boolean> getter;
    private final Boolean value;

    public BooleanCheck(Function<T, Boolean> getter, Boolean value) {
        this.getter = getter;
        this.value = value;
    }

    @Override
    public boolean test(T data) {
        return Objects.equals(getter.apply(data), value);
    }
}
