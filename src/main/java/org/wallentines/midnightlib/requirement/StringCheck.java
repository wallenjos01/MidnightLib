package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class StringCheck<T> implements Check<T> {

    public static <T> CheckType<T> type(Function<T, String> getter) {
        return type(getter, StringCheck::new);
    }

    public static <T> CheckType<T> type(Function<T, String> getter, Functions.F2<Function<T, String>, Collection<String>, ? extends Check<T>> constructor) {
        return new CheckType<T>() {
            @Override
            public <O> SerializeResult<Check<T>> deserialize(SerializeContext<O> context, O value) {
                return STRING_SERIALIZER.fieldOf("value").deserialize(context, value).flatMap(str -> constructor.apply(getter, str));
            }
        };
    }

    public static final Serializer<Collection<String>> STRING_SERIALIZER = Serializer.STRING.listOf().or(Serializer.STRING.flatMap(col -> col.iterator().next(), List::of));


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
    public boolean check(T t) {
        return values.contains(getter.apply(t));
    }

    @Override
    public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
        return STRING_SERIALIZER.fieldOf("value").serialize(context, values);
    }
}
