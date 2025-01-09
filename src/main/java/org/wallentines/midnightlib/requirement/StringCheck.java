package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.TypeReference;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class StringCheck<T> implements Check<T> {

    protected final Type<T> type;
    protected final Set<String> values;

    public StringCheck(Type<T> type, Collection<String> values) {
        this.type = type;
        this.values = Set.copyOf(values);
    }

    @Override
    public boolean check(T t) {
        return values.contains(type.getter.apply(t));
    }

    @Override
    public Type<T> type() {
        return type;
    }

    public Set<String> values() {
        return values;
    }

    public static class Type<T> implements CheckType<T, StringCheck<T>> {

        private final Function<T, String> getter;
        private final Serializer<StringCheck<T>> serializer;

        public Type(Function<T, String> getter) {
            this.getter = getter;
            this.serializer = Serializer.STRING.listOf().mapToSet()
                    .or(Serializer.STRING.flatMap(col -> col.iterator().next(), Set::of))
                    .flatMap(StringCheck::values, values -> new StringCheck<>(this, values))
                    .fieldOf("value");
        }

        @Override
        public Serializer<StringCheck<T>> serializer() {
            return serializer;
        }
    }

}
