package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.Objects;
import java.util.function.Function;

public class NumberRequirement<T> extends Requirement<T> {

    public static <T> RequirementType<T> type(Function<T, Number> getter) {
        return new RequirementType<>() {
            @Override
            public <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value) {
                return SerializeResult.ofNullable(ctx.asNumber(value), "Expected a Number!").flatMap(num -> new NumberRequirement<>(this, getter, num));
            }
        };
    }

    private final Function<T, Number> getter;
    private final Number value;

    public NumberRequirement(RequirementType<T> type, Function<T, Number> getter, Number value) {
        super(type);
        this.getter = getter;
        this.value = value;
    }

    @Override
    public boolean check(T data) {
        return Objects.equals(getter.apply(data), value);
    }

    @Override
    public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {
        return SerializeResult.success(ctx.toNumber(value));
    }
}
