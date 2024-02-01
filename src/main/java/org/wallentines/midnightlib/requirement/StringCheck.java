package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringCheck<T> implements Predicate<T> {

    public static <T> Serializer<StringCheck<T>> serializer(Function<T, String> getter) {

        return serializer(req -> req.values, values -> new StringCheck<>(getter, values));
    }

    public static <S> Serializer<S> serializer(Function<S, Collection<String>> backGetter, Function<Collection<String>, S> constructor) {

        return STRING_SERIALIZER.fieldOf("value").map(backGetter, constructor);
    }

    public static final Serializer<Collection<String>> STRING_SERIALIZER = Serializer.STRING.listOf().or(Serializer.STRING.map(col -> col.iterator().next(), List::of));


    protected final Function<T, String> getter;
    protected final Set<String> values;

    public StringCheck(Function<T, String> getter, String value) {
        this.getter = getter;
        this.values = Set.of(value);
    }

    public StringCheck(Function<T, String> getter, Collection<String> values) {
        this.getter = getter;
        this.values = Set.copyOf(values);
    }

    @Override
    public boolean test(T t) {
        return values.contains(getter.apply(t));
    }
}
