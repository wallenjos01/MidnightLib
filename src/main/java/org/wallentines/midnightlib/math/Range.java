package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class representing a range or set of comparable objects, usually numbers
 * @param <T> The type of objects to compare
 */
public interface Range<T extends Comparable<T>> {

    /**
     * Determines if the given value is considered to be within the range
     * @param value The value to check
     * @return Whether the value is in the range
     */
    boolean isWithin(T value);


    class Supplied<T extends Comparable<T>> implements Range<T> {
        private final Supplier<T> getter;

        private Supplied(Supplier<T> getter) {
            this.getter = getter;
        }

        @Override
        public boolean isWithin(T value) {
            return getter.get().compareTo(value) == 0;
        }
    }

    /**
     * Returns true for all values
     * @param <T> The type of values to compare
     */
    class All<T extends Comparable<T>> implements Range<T> {

        @Override
        public boolean isWithin(T value) { return true; }
        public static <T extends Comparable<T>> Serializer<All<T>> serializer() {
            return InlineSerializer.of(val -> "all", str -> {
                if(str.equals("all")) return new All<>();
                return null;
            });
        }
    }

    /**
     * Returns true if the given value is equal to a specified value
     * @param <T> The type of values to compare
     */
    class Exact<T extends Comparable<T>> implements Range<T> {
        public final T number;

        private Exact(T number) {
            this.number = number;
        }

        @Override
        public boolean isWithin(T value) {
            return number.compareTo(value) == 0;
        }

        public static <T extends Comparable<T>> Serializer<Exact<T>> serializer(Function<Number, T> converter, Function<T, Number> backConverter, Function<String, T> parser) {
            return new Serializer<Exact<T>>() {
                @Override
                public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, Exact<T> tExact) {
                    return SerializeResult.success(ctx.toNumber(backConverter.apply(tExact.number)));
                }
                @Override
                public <O> SerializeResult<Exact<T>> deserialize(SerializeContext<O> ctx, O o) {
                    if (ctx.isNumber(o)) {
                        return SerializeResult.success(new Exact<>(converter.apply(ctx.asNumber(o))));
                    } else if (ctx.isString(o)) {
                        try {
                            return SerializeResult.success(new Exact<>(parser.apply(ctx.asString(o))));
                        } catch (Exception ex) {
                            return SerializeResult.failure("An error occurred while trying to parse a number from a String!");
                        }
                    }
                    return null;
                }
            };
        }
    }

    /**
     * Returns true if the given value is equal to one of a collection of specified values
     * @param <T> The type of values to compare
     */
    class Roster<T extends Comparable<T>> implements Range<T> {
        private final Set<T> values;

        private Roster(Collection<T> values) {
            this.values = Set.copyOf(values);
        }

        @Override
        public boolean isWithin(T value) {
            for(T t : values) {
                if(t.compareTo(value) == 0) return true;
            }
            return false;
        }

        public static <T extends Comparable<T>> Serializer<Roster<T>> serializer(Function<Number, T> converter, Function<T, Number> backConverter, Function<String, T> parser) {
            return new Serializer<Roster<T>>() {
                @Override
                public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, Roster<T> exact) {
                    List<O> out = new ArrayList<>();
                    for(T t : exact.values) {
                        out.add(ctx.toNumber(backConverter.apply(t)));
                    }
                    return SerializeResult.success(ctx.toList(out));
                }
                @Override
                public <O> SerializeResult<Roster<T>> deserialize(SerializeContext<O> ctx, O o) {
                    if (ctx.isList(o)) {

                        List<T> out = new ArrayList<>();
                        for(O o1 : ctx.asList(o)) {
                            if(!ctx.isNumber(o1)) {
                                return SerializeResult.failure("Unable to parse list of numbers!");
                            }
                            out.add(converter.apply(ctx.asNumber(o1)));
                        }
                        return SerializeResult.success(new Roster<>(out));

                    } else if (ctx.isString(o)) {

                        String s = ctx.asString(o);

                        String[] parts = s.split(",");
                        String firstPart = parts[0];
                        String lastPart = parts[parts.length - 1];

                        if (firstPart.length() <= 1 || lastPart.length() <= 1) {
                            return SerializeResult.failure("Not enough data to form a roster!");
                        }

                        char firstChar = firstPart.charAt(0);
                        char lastChar = lastPart.charAt(lastPart.length() - 1);
                        parts[0] = firstPart.substring(1);
                        parts[parts.length - 1] = lastPart.substring(0, lastPart.length() - 2);

                        if (firstChar == '{') {
                            if (lastChar != '}') {
                                return SerializeResult.failure("Expected a } at the end of a roster!");
                            }

                            try {
                                List<T> out = new ArrayList<>(parts.length);
                                for (String part : parts) {
                                    out.add(parser.apply(part));
                                }
                                return SerializeResult.success(new Roster<>(out));

                            } catch (Exception ex) {
                                return SerializeResult.failure("Failed to parse a number in a number set! " + ex.getMessage());
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }

    /**
     * Returns true if the given value satisfies comparison operators (<, >, <=, >=)
     * @param <T> The type of values to compare
     */
    class Comparison<T extends Comparable<T>> implements Range<T> {

        public final T value;
        public final boolean greater;
        public final boolean equal;

        private Comparison(T value, boolean greater, boolean equal) {
            this.value = value;
            this.greater = greater;
            this.equal = equal;
        }

        @Override
        public boolean isWithin(T val) {
            int comp = val.compareTo(value);
            return greater
                    ? equal
                        ? comp >= 0
                        : comp > 0
                    : equal
                        ? comp <= 0
                        : comp < 0;
        }

        public static <T extends Comparable<T>> Serializer<Comparison<T>> serializer(Function<T, Number> backConverter, Function<String, T> parser) {
            return new Serializer<Comparison<T>>() {
                @Override
                public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, Comparison<T> cmp) {
                    StringBuilder str = new StringBuilder(cmp.greater ? ">" : "<");
                    if(cmp.equal) str.append("=");

                    str.append(backConverter.apply(cmp.value));
                    return SerializeResult.success(ctx.toString(str.toString()));
                }

                @Override
                public <O> SerializeResult<Comparison<T>> deserialize(SerializeContext<O> ctx, O o) {
                    if(!ctx.isString(o)) return SerializeResult.failure("Expected comparison to be a string!");
                    String s = ctx.asString(o);

                    if(s.charAt(0) != '>' && s.charAt(0) != '<') {
                        return SerializeResult.failure("Expected comparison to start with a < or >!");
                    }

                    boolean greater = s.charAt(0) == '>';
                    boolean equals = s.charAt(1) == '=';

                    try {
                        T out = parser.apply(s.substring(1 + (equals ? 1 : 0)));
                        return SerializeResult.success(new Comparison<>(out, greater, equals));

                    } catch (Exception ex) {
                        return SerializeResult.failure("Failed to parse a number! " + ex.getMessage());
                    }
                }
            };
        }
    }

    /**
     * Returns true if the given value is within a certain interval
     * @param <T> The type of values to compare
     */
    class Interval<T extends Comparable<T>> implements Range<T> {

        final T lower;
        final T upper;
        final boolean lowerOpen;
        final boolean upperOpen;

        private Interval(T lower, T upper, boolean lowerOpen, boolean upperOpen) {
            this.lower = lower;
            this.upper = upper;
            this.lowerOpen = lowerOpen;
            this.upperOpen = upperOpen;
        }

        @Override
        public boolean isWithin(T val) {
            int compL = val.compareTo(lower);
            int compU = val.compareTo(upper);
            return lowerOpen
                    ? upperOpen
                        ? compL > 0 && compU < 0
                        : compL > 0 && compU <= 0
                    : upperOpen
                        ? compL >= 0 && compU < 0
                        : compL >= 0 && compU <= 0;
        }

        public static <T extends Comparable<T>> Serializer<Interval<T>> serializer(Function<T, Number> backConverter, Function<String, T> parser) {
            return new Serializer<Interval<T>>() {
                @Override
                public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, Interval<T> in) {
                    return SerializeResult.success(ctx.toString((in.lowerOpen ? "(" : "[") +
                            backConverter.apply(in.lower) +
                            "," +
                            backConverter.apply(in.upper) +
                            (in.upperOpen ? ")" : "]")));
                }

                @Override
                public <O> SerializeResult<Interval<T>> deserialize(SerializeContext<O> ctx, O o) {
                    if (!ctx.isString(o)) return SerializeResult.failure("Expected interval to be a string!");
                    String s = ctx.asString(o);

                    if (s.charAt(0) != '[' && s.charAt(0) != '(' || s.charAt(s.length() - 1) != ']' && s.charAt(s.length() - 1) != ')') {
                        return SerializeResult.failure("Expected interval to start and end with ( or [ and ) or ]!");
                    }

                    String[] values = s.substring(1, s.length() - 1).split(",");
                    if(values.length != 2) {
                        return SerializeResult.failure("Expected interval to have exactly 2 elements!");
                    }

                    boolean lowerOpen = s.charAt(0) == '(';
                    boolean upperOpen = s.charAt(s.length() - 1) == ')';


                    try {
                        T lower = parser.apply(values[0]);
                        T upper = parser.apply(values[1]);
                        return SerializeResult.success(new Interval<>(lower, upper, lowerOpen, upperOpen));

                    } catch (Exception ex) {
                        return SerializeResult.failure("Failed to parse a number! " + ex.getMessage());
                    }
                }
            };
        }
    }

    static <T extends Comparable<T>> Range<T> all() {
        return new All<>();
    }
    static <T extends Comparable<T>> Range<T> exactly(T value) {
        return new Exact<>(value);
    }
    static <T extends Comparable<T>> Range<T> inSet(Collection<T> values) {
        return new Roster<>(values);
    }
    static <T extends Comparable<T>> Range<T> lessThan(T value) {
        return new Comparison<>(value, false, false);
    }
    static <T extends Comparable<T>> Range<T> greaterThan(T value) {
        return new Comparison<>(value, true, false);
    }
    static <T extends Comparable<T>> Range<T> atMost(T value) {
        return new Comparison<>(value, false, true);
    }
    static <T extends Comparable<T>> Range<T> atLeast(T value) {
        return new Comparison<>(value, true, true);
    }
    static <T extends Comparable<T>> Range<T> openInterval(T lower, T upper) {
        return new Interval<>(lower, upper, true, true);
    }
    static <T extends Comparable<T>> Range<T> closedInterval(T lower, T upper) {
        return new Interval<>(lower, upper, false, false);
    }
    static <T extends Comparable<T>> Range<T> openClosedInterval(T lower, T upper) {
        return new Interval<>(lower, upper, true, false);
    }
    static <T extends Comparable<T>> Range<T> closedOpenInterval(T lower, T upper) {
        return new Interval<>(lower, upper, false, true);
    }
    static <T extends Comparable<T>> Range<T> supplied(Supplier<T> supplier) {
        return new Supplied<>(supplier);
    }

    Serializer<Range<Integer>> INTEGER = forNumber(Number::intValue, i -> i, Integer::parseInt);
    Serializer<Range<Long>> LONG = forNumber(Number::longValue, i -> i, Long::parseLong);
    Serializer<Range<Double>> DOUBLE = forNumber(Number::doubleValue, i -> i, Double::parseDouble);


    static <T extends Comparable<T>> Serializer<Range<T>> forNumber(Function<Number, T> converter, Function<T, Number> backConverter, Function<String, T> parser) {

        return new Serializer<Range<T>>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, Range<T> range) {

                if (range instanceof Range.Exact) {
                    Range.Exact.serializer(converter, backConverter, parser).serialize(ctx, (Range.Exact<T>) range);
                } else if (range instanceof Range.All) {
                    Range.All.serializer().serialize(ctx, null);
                } else if (range instanceof Range.Roster) {
                    Range.Roster.serializer(converter, backConverter, parser).serialize(ctx, (Range.Roster<T>) range);
                } else if (range instanceof Range.Comparison) {
                    Range.Comparison.serializer(backConverter, parser).serialize(ctx, (Range.Comparison<T>) range);
                } else if (range instanceof Range.Interval) {
                    Range.Interval.serializer(backConverter, parser).serialize(ctx, (Range.Interval<T>) range);
                }

                return SerializeResult.failure("Don't know how to serialize " + range);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <O> SerializeResult<Range<T>> deserialize(SerializeContext<O> ctx, O o) {
                return Range.Exact.serializer(converter, backConverter, parser).deserialize(ctx, o).flatMap(ex -> (Range<T>) ex)
                        .mapError(() -> Range.All.serializer().deserialize(ctx, o).flatMap(all -> (Range<T>) all))
                        .mapError(() -> Range.Roster.serializer(converter, backConverter, parser).deserialize(ctx, o).flatMap(all -> all))
                        .mapError(() -> Range.Comparison.serializer(backConverter, parser).deserialize(ctx, o).flatMap(all -> all))
                        .mapError(() -> Range.Interval.serializer(backConverter, parser).deserialize(ctx, o).flatMap(all -> all));
            }
        };

    }
}
