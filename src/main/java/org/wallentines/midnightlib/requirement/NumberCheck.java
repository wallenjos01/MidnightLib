package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;

import java.util.function.Function;
import java.util.function.Predicate;

public class NumberCheck<T, N extends Comparable<N>> implements Predicate<T> {

    public static <T> Serializer<NumberCheck<T, Integer>> forInt(Function<T, Integer> getter) {
        return serializer(getter, Range.INTEGER);
    }

    public static <T> Serializer<NumberCheck<T, Long>> forLong(Function<T, Long> getter) {
        return serializer(getter, Range.LONG);
    }

    public static <T> Serializer<NumberCheck<T, Double>> forDouble(Function<T, Double> getter) {
        return serializer(getter, Range.DOUBLE);
    }

    public static <T, N extends Comparable<N>> Serializer<NumberCheck<T, N>> serializer(Function<T, N> getter, Serializer<Range<N>> serializer) {

        return serializer(serializer, NumberCheck::getRange, range -> new NumberCheck<>(getter, range));
    }

    public static <N extends Comparable<N>, R> Serializer<R> serializer(Serializer<Range<N>> serializer, Function<R, Range<N>> backGetter, Function<Range<N>, R> constructor) {

        return serializer.fieldOf("value").map(backGetter, constructor);
    }

    private final Function<T, N> getter;
    private final Range<N> range;

    public NumberCheck(Function<T, N> getter, Range<N> range) {
        this.getter = getter;
        this.range = range;
    }

    public Range<N> getRange() {
        return range;
    }

    @Override
    public boolean test(T t) {
        return range.isWithin(getter.apply(t));
    }

}
