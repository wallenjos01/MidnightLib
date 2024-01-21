package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.Objects;
import java.util.function.Function;

public class BooleanRequirement<T> extends Requirement<T> {

    public static <T> RequirementType<T> type(Function<T, Boolean> getter) {
        return new RequirementType<>() {
            @Override
            public <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value) {
                return SerializeResult.ofNullable(ctx.asBoolean(value), "Expected a Boolean!").flatMap(b -> new BooleanRequirement<>(this, getter, b));
            }
        };
    }

    private final Function<T, Boolean> getter;
    private final Boolean value;

    public BooleanRequirement(RequirementType<T> type, Function<T, Boolean> getter, Boolean value) {
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
        return SerializeResult.success(ctx.toBoolean(value));
    }
}
