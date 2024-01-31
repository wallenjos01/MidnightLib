package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringRequirement<T> implements Predicate<T> {

    public static <T> Serializer<StringRequirement<T>> serializer(Function<T, String> getter) {

        return Serializer.STRING.listOf()
                .or(Serializer.STRING
                        .map(col -> col.iterator().next(), List::of)
                )
                .map(ser -> ser.values, val -> new StringRequirement<>(getter, val));
    }

    private final Function<T, String> getter;
    private final Set<String> values;

    public StringRequirement(Function<T, String> getter, String value) {
        this.getter = getter;
        this.values = Set.of(value);
    }

    public StringRequirement(Function<T, String> getter, Collection<String> values) {
        this.getter = getter;
        this.values = Set.copyOf(values);
    }

    @Override
    public boolean test(T t) {
        return values.contains(getter.apply(t));
    }
}
