package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.*;
import org.wallentines.mdcfg.registry.Registry;
import org.wallentines.midnightlib.math.Range;

import java.util.Collection;

public class CompositeCheck<T> implements Check<T> {

    private final Type<T> type;
    private final Collection<Requirement<T>> requirements;
    private final Range<Integer> count;

    public CompositeCheck(Type<T> type, Collection<Requirement<T>> requirements, Range<Integer> count) {
        this.type = type;
        this.requirements = requirements;
        this.count = count;
    }

    @Override
    public boolean check(T data) {
        return checkAll(requirements, count, data);
    }

    @Override
    public Type<T> type() {
        return type;
    }

    public Collection<Requirement<T>> checks() {
        return requirements;
    }

    public Range<Integer> count() {
        return count;
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

        @Override
        public Serializer<CompositeCheck<T>> serializer() {
            return serializer;
        }

    }

    public static <T, R extends Requirement<T>> boolean checkAll(Collection<R> requirements, Range<Integer> count, T data) {
        Range<Integer> effectiveRange;
        int minBound = -1;

        if(count instanceof Range.All) {
            effectiveRange = Range.exactly(requirements.size());
            minBound = requirements.size();
        } else {
            effectiveRange = count;
            if(count instanceof Range.Exact) {
                minBound = ((Range.Exact<Integer>) count).number;
            } else if(count instanceof Range.Comparison) {

                Range.Comparison<Integer> cmp = (Range.Comparison<Integer>) count;
                if(cmp.greater) {
                    minBound = cmp.value;
                    if(!cmp.equal) minBound++;
                }
            }
        }

        int checked = 0;
        int remaining = requirements.size();
        for(R req : requirements) {
            if(checked + remaining < minBound) return false;
            if(req.check(data)) {
                checked++;
            }
            remaining--;
        }

        return effectiveRange.isWithin(checked);
    }

}
