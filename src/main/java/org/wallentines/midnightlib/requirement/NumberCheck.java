package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.TypeReference;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;

import java.util.function.Function;

public class NumberCheck<T, N extends Comparable<N>> implements Check<T> {

    private final Type<T, N> type;
    private final Range<N> range;

    public NumberCheck(Type<T, N> type, Range<N> range) {
        this.type = type;
        this.range = range;
    }

    public Range<N> range() {
        return range;
    }

    @Override
    public boolean check(T t) {
        return range.isWithin(type.getter.apply(t));
    }

    @Override
    public Type<T, N> type() {
        return type;
    }


    public static class Type<T, N extends Comparable<N>> implements CheckType<T, NumberCheck<T, N>> {

        private final Function<T, N> getter;
        private final Serializer<NumberCheck<T, N>> serializer;

        public Type(Function<T, N> getter, Serializer<Range<N>> rangeSerializer) {
            this.getter = getter;
            this.serializer = rangeSerializer.fieldOf("value").flatMap(NumberCheck::range, range -> new NumberCheck<>(this, range));
        }

        @Override
        public TypeReference<NumberCheck<T, N>> type() {
            return new TypeReference<NumberCheck<T, N>>() {};
        }

        @Override
        public Serializer<NumberCheck<T, N>> serializer() {
            return serializer;
        }


        public static <T> Type<T, Integer> forInt(Function<T, Integer> getter) {
            return new Type<>(getter, Range.INTEGER);
        }

        public static <T> Type<T, Long> forLong(Function<T, Long> getter) {
            return new Type<>(getter, Range.LONG);
        }

        public static <T> Type<T, Double> forDouble(Function<T, Double> getter) {
            return new Type<>(getter, Range.DOUBLE);
        }

    }

}
