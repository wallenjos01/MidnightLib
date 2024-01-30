package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.Objects;
import java.util.function.Function;

public class BooleanRequirement<T> extends Requirement<T> {

    public static <T> RequirementType<T> type(Function<T, Boolean> getter) {
        return type(getter, BooleanRequirement::new);
    }

    public static <T> RequirementType<T> type(Function<T, Boolean> getter, Functions.F4<RequirementType<T>, Boolean, Function<T, Boolean>, Boolean, Requirement<T>> builder) {
        return new RequirementType<>() {
            @Override
            public <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value, boolean invert) {
                return SerializeResult.ofNullable(ctx.asBoolean(value), "Expected a Boolean!").flatMap(b -> builder.apply(this, invert, getter, b));
            }
        };
    }

    private final Function<T, Boolean> getter;
    private final Boolean value;

    public BooleanRequirement(RequirementType<T> type, boolean invert, Function<T, Boolean> getter, Boolean value) {
        super(type, invert);
        this.getter = getter;
        this.value = value;
    }

    @Override
    protected boolean doCheck(T data) {
        return Objects.equals(getter.apply(data), value);
    }

    @Override
    public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {
        return SerializeResult.success(ctx.toBoolean(value));
    }
}
