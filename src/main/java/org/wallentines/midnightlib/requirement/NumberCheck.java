package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;

import java.util.function.Function;

public class NumberCheck<T, N extends Comparable<N>> implements Check<T> {

    public static <T> CheckType<T> forInt(Function<T, Integer> getter) {
        return type(getter, Range.INTEGER);
    }

    public static <T> CheckType<T>forLong(Function<T, Long> getter) {
        return type(getter, Range.LONG);
    }

    public static <T> CheckType<T> forDouble(Function<T, Double> getter) {
        return type(getter, Range.DOUBLE);
    }

    public static <T, N extends Comparable<N>> CheckType<T> type(Function<T, N> getter, Serializer<Range<N>> serializer) {

        return type(serializer, range -> new NumberCheck<>(serializer, getter, range));
    }

    public static <T, N extends Comparable<N>> CheckType<T> type(Serializer<Range<N>> serializer, Function<Range<N>, Check<T>> constructor) {
        return new CheckType<T>() {
            @Override
            public <O> SerializeResult<Check<T>> deserialize(SerializeContext<O> context, O value) {
                return serializer.fieldOf("value").deserialize(context, value).flatMap(constructor);
            }
        };
    }

    private final Serializer<Range<N>> serializer;
    private final Function<T, N> getter;
    private final Range<N> range;

    public NumberCheck(Serializer<Range<N>> serializer, Function<T, N> getter, Range<N> range) {
        this.serializer = serializer;
        this.getter = getter;
        this.range = range;
    }

    public Range<N> getRange() {
        return range;
    }

    @Override
    public boolean check(T t) {
        return range.isWithin(getter.apply(t));
    }

    @Override
    public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
        return serializer.fieldOf("value").serialize(context, range);
    }

}
