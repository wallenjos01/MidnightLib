package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;

import java.util.Collection;
import java.util.List;

public class CompositeCheck<V, T extends CheckType<V>, R extends Requirement<V, T>> implements Check<V> {

    protected final Serializer<R> generalSerializer;
    protected final List<R> requirements;
    protected final Range<Integer> range;

    public CompositeCheck(Range<Integer> range, Collection<R> requirements) {
        this.generalSerializer = null;
        this.range = range;
        this.requirements = List.copyOf(requirements);
    }


    public CompositeCheck(Serializer<R> generalSerializer, Range<Integer> range, Collection<R> requirements) {
        this.generalSerializer = generalSerializer;
        this.range = range;
        this.requirements = List.copyOf(requirements);
    }

    @Override
    public boolean check(V data) {
        return checkAll(range, requirements, data);
    }

    @Override
    public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
        if(generalSerializer == null) {
            return SerializeResult.failure("This check is not serializable!");
        }
        return serializer(generalSerializer).serialize(context, this);
    }

    public List<R> getRequirements() {
        return requirements;
    }

    public Range<Integer> getRange() {
        return range;
    }

    public static <V> CheckType<V> type(Serializer<Requirement<V,CheckType<V>>> ser) {
        return new CheckType<V>() {
            @Override
            public <O> SerializeResult<Check<V>> deserialize(SerializeContext<O> context, O value) {
                return serializer(ser).deserialize(context, value).flatMap(chk -> chk);
            }
        };
    }

    public static <V, T extends CheckType<V>, R extends Requirement<V,T >> Serializer<CompositeCheck<V, T, R>> serializer(Serializer<R> generalSerializer) {
        return serializer(generalSerializer, CompositeCheck::new);
    }

    public static <V, T extends CheckType<V>, R extends Requirement<V,T>, C extends CompositeCheck<V,T,R>> Serializer<C> serializer(Serializer<R> serializer, Functions.F3<Serializer<R>, Range<Integer>, Collection<R>, C> constructor) {
        return ObjectSerializer.create(
                Range.INTEGER.<C>entry("count", CompositeCheck::getRange).optional(),
                serializer.listOf().entry("values", CompositeCheck::getRequirements),
                (range, list) -> constructor.apply(serializer, range, list)
        );
    }

    public static <V, T extends CheckType<V>, R extends Requirement<V,T>> boolean checkAll(Range<Integer> range, Collection<R> requirements, V data) {

        Range<Integer> effectiveRange = range instanceof Range.All ? Range.exactly(requirements.size()) : range;

        int checked = 0;
        for(Requirement<V,T> r : requirements) {
            if(r.check(data)) {
                checked++;
            }
        }

        return effectiveRange.isWithin(checked);
    }

}
