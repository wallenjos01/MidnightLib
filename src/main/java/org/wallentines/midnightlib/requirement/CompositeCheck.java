package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;

import java.util.Collection;
import java.util.List;

public class CompositeCheck<V, T extends CheckType<V>> implements Check<V> {

    protected final Serializer<Requirement<V,T>> generalSerializer;
    protected final List<Requirement<V,T>> requirements;
    protected final Range<Integer> range;

    public CompositeCheck(Range<Integer> range, Collection<Requirement<V,T>> requirements) {
        this.generalSerializer = null;
        this.range = range;
        this.requirements = List.copyOf(requirements);
    }


    public CompositeCheck(Serializer<Requirement<V,T>> generalSerializer, Range<Integer> range, Collection<Requirement<V,T>> requirements) {
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

    public List<Requirement<V,T>> getRequirements() {
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

    public static <V, T extends CheckType<V>> Serializer<CompositeCheck<V, T>> serializer(Serializer<Requirement<V,T>> serializer) {
        return ObjectSerializer.create(
                Range.INTEGER.<CompositeCheck<V,T>>entry("count", CompositeCheck::getRange).optional(),
                serializer.listOf().entry("values", CompositeCheck::getRequirements),
                (range, list) -> new CompositeCheck<>(serializer, range, list)
        );
    }
    public static <V, T extends CheckType<V>> boolean checkAll(Range<Integer> range, Collection<Requirement<V,T>> requirements, V data) {

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
