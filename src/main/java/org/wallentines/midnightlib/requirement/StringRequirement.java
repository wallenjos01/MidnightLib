package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.Objects;
import java.util.function.Function;

public class StringRequirement<T> extends Requirement<T> {

    public static <T> RequirementType<T> type(Function<T, String> getter) {
        return new RequirementType<>() {
            @Override
            public <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value) {
                return SerializeResult.ofNullable(ctx.asString(value), "Expected a String!").flatMap(str -> new StringRequirement<>(this, getter, str));
            }
        };
    }

    private final Function<T, String> getter;
    private final String value;

    public StringRequirement(RequirementType<T> type, Function<T, String> getter, String value) {
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
        return SerializeResult.success(ctx.toString(value));
    }
}
