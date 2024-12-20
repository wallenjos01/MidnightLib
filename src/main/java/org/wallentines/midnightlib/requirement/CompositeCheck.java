package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.TypeReference;
import org.wallentines.mdcfg.serializer.*;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.Registry;

import java.util.Collection;

public class CompositeCheck<T> implements Check<T> {

    private final Type<T> type;
    private final Collection<Requirement<T>> checks;
    private final Range<Integer> range;

    public CompositeCheck(Type<T> type, Collection<Requirement<T>> checks, Range<Integer> range) {
        this.type = type;
        this.checks = checks;
        this.range = range;
    }

    @Override
    public boolean check(T data) {
        Range<Integer> effectiveRange;
        int minBound = -1;

        if(range instanceof Range.All) {
            effectiveRange = Range.exactly(checks.size());
        } else {
            effectiveRange = range;
            if(range instanceof Range.Exact) {
                minBound = ((Range.Exact<Integer>) range).number;
            } else if(range instanceof Range.Comparison) {

                Range.Comparison<Integer> cmp = (Range.Comparison<Integer>) range;
                if(cmp.greater) {
                    minBound = cmp.value;
                    if(!cmp.equal) minBound++;
                }
            }
        }

        int checked = 0;
        int remaining = checks.size();
        for(Requirement<T> req : checks) {
            if(checked + remaining < minBound) return false;
            if(req.check(data)) {
                checked++;
            }
            remaining--;
        }

        return effectiveRange.isWithin(checked);
    }

    @Override
    public Type<T> type() {
        return type;
    }

    public Collection<Requirement<T>> checks() {
        return checks;
    }

    public Range<Integer> count() {
        return range;
    }

    public static class Type<T> implements CheckType<T, CompositeCheck<T>> {

        private final Serializer<CompositeCheck<T>> serializer;

        public Type(Registry<?, CheckType<T, ?>> typeRegistry) {
            this.serializer = ObjectSerializer.create(
                    Requirement.serializer(typeRegistry).listOf().entry("values", CompositeCheck::checks),
                    Range.INTEGER.entry("count", CompositeCheck<T>::count),
                    (values, count) -> new CompositeCheck<>(this, values, count)
            );

        }

        static <T, C extends Check<T>> Serializer<Check<T>> generify(CheckType<T, C> type) {
            Serializer<C> serializer = type.serializer();
            TypeReference<C> ref = type.type();

            return serializer.cast(ref, new TypeReference<Check<T>>() {});
        }

        @Override
        public TypeReference<CompositeCheck<T>> type() {
            return new TypeReference<CompositeCheck<T>>() {};
        }

        @Override
        public Serializer<CompositeCheck<T>> serializer() {
            return serializer;
        }

    }


//    protected final Serializer<R> generalSerializer;
//    protected final List<R> requirements;
//    protected final Range<Integer> range;
//
//    public CompositeCheck(Range<Integer> range, Collection<R> requirements) {
//        this.generalSerializer = null;
//        this.range = range;
//        this.requirements = List.copyOf(requirements);
//    }
//
//
//    public CompositeCheck(Serializer<R> generalSerializer, Range<Integer> range, Collection<R> requirements) {
//        this.generalSerializer = generalSerializer;
//        this.range = range;
//        this.requirements = List.copyOf(requirements);
//    }
//
//    @Override
//    public boolean check(V data) {
//        return checkAll(range, requirements, data);
//    }
//
//    @Override
//    public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
//        if(generalSerializer == null) {
//            return SerializeResult.failure("This check is not serializable!");
//        }
//        return serializer(generalSerializer).serialize(context, this);
//    }
//
//    public List<R> getRequirements() {
//        return requirements;
//    }
//
//    public Range<Integer> getRange() {
//        return range;
//    }
//
//    public static <V> CheckType<V> type(Serializer<Requirement<V,CheckType<V>>> ser) {
//        return new CheckType<V>() {
//            @Override
//            public Serializer<Check<V>> serializer() {
//                return serializer(ser);
//            }
//        };
//    }
//
//    public static <V, T extends CheckType<V>, R extends Requirement<V,T >> Serializer<CompositeCheck<V, T, R>> serializer(Serializer<R> generalSerializer) {
//        return serializer(generalSerializer, CompositeCheck::new);
//    }
//
//    public static <V, T extends CheckType<V>, R extends Requirement<V,T>, C extends CompositeCheck<V,T,R>> Serializer<C> serializer(Serializer<R> serializer, Functions.F3<Serializer<R>, Range<Integer>, Collection<R>, C> constructor) {
//        return ObjectSerializer.create(
//                Range.INTEGER.<C>entry("count", CompositeCheck::getRange).optional(),
//                serializer.listOf().entry("values", CompositeCheck::getRequirements),
//                (range, list) -> constructor.apply(serializer, range, list)
//        );
//    }
//
//    public static <V, T extends CheckType<V>, R extends Requirement<V,T>> boolean checkAll(Range<Integer> range, Collection<R> requirements, V data) {
//
//        Range<Integer> effectiveRange;
//        int minBound = -1;
//
//        if(range instanceof Range.All) {
//            effectiveRange = Range.exactly(requirements.size());
//        } else {
//            effectiveRange = range;
//            if(range instanceof Range.Exact) {
//                minBound = ((Range.Exact<Integer>) range).number;
//            } else if(range instanceof Range.Comparison) {
//
//                Range.Comparison<Integer> cmp = (Range.Comparison<Integer>) range;
//                if(cmp.greater) {
//                    minBound = cmp.value;
//                    if(!cmp.equal) minBound++;
//                }
//
//            }
//        }
//
//        int checked = 0;
//        int remaining = requirements.size();
//        for(Requirement<V,T> r : requirements) {
//            if(checked + remaining < minBound) return false;
//            if(r.check(data)) {
//                checked++;
//            }
//            remaining--;
//        }
//
//        return effectiveRange.isWithin(checked);
//    }

}
