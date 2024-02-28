package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;
import java.util.function.Function;

public class BooleanCheck<T> implements Check<T> {

    public static <T> CheckType<T> type(Function<T, Boolean> getter) {
        return new CheckType<T>() {
            @Override
            public <O> SerializeResult<Check<T>> deserialize(SerializeContext<O> context, O value) {
                return Serializer.BOOLEAN.fieldOf("value").deserialize(context, value).flatMap(b -> new BooleanCheck<>(getter, b));
            }
        };
    }

    private final Function<T, Boolean> getter;
    private final Boolean value;

    public BooleanCheck(Function<T, Boolean> getter, Boolean value) {
        this.getter = getter;
        this.value = value;
    }

    @Override
    public boolean check(T data) {
        return Objects.equals(getter.apply(data), value);
    }

    @Override
    public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
        return Serializer.BOOLEAN.fieldOf("value").serialize(context, value);
    }
}
