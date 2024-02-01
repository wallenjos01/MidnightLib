package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class CompositeCheck<T, R extends Requirement<T, Predicate<T>>> implements Predicate<T> {

    protected final List<R> requirements;
    protected final Range<Integer> range;

    public CompositeCheck(Range<Integer> range, Collection<R> requirements) {
        this.range = range;
        this.requirements = List.copyOf(requirements);
    }

    @Override
    public boolean test(T t) {
        return checkAll(range, requirements, t);
    }

    public List<R> getRequirements() {
        return requirements;
    }

    public Range<Integer> getRange() {
        return range;
    }

    public static <T, R extends Requirement<T, Predicate<T>>> Serializer<Predicate<T>> serializer(Serializer<R> serializer) {
        return serializer(
                cc -> ((CompositeCheck<T,R>) cc).range,
                cc -> ((CompositeCheck<T,R>) cc).requirements,
                (range, requirements) -> new CompositeCheck<>(range, requirements), serializer);
    }

    public static <T, P extends Predicate<T>, R extends Requirement<T, P>> Serializer<P> serializer(Function<P, Range<Integer>> rangeGetter, Function<P, Collection<R>> requirementGetter, BiFunction<Range<Integer>, Collection<R>, P> constructor, Serializer<R> serializer) {
        return ObjectSerializer.create(
                Range.INTEGER.entry("count", rangeGetter).orElse(Range.all()),
                serializer.listOf().entry("values", requirementGetter),
                constructor::apply
        );
    }

    public static <T, P extends Predicate<T>, R extends Requirement<T, P>> boolean checkAll(Range<Integer> range, Collection<R> requirements, T data) {


        Range<Integer> effectiveRange = range instanceof Range.All ? Range.exactly(requirements.size()) : range;

        int checked = 0;
        for(R r : requirements) {
            if(r.check(data)) {
                checked++;
            }
        }

        return effectiveRange.isWithin(checked);
    }

}
