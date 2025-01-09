package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.TypeReference;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.function.Function;

public class BooleanCheck<T> implements Check<T> {

    private final Type<T> type;
    private final Boolean value;

    public BooleanCheck(Type<T> type, Boolean value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean check(T data) {
        return type.getter.apply(data) == value;
    }

    @Override
    public Type<T> type() {
        return type;
    }

    public Boolean value() {
        return value;
    }

    public static class Type<T> implements CheckType<T, BooleanCheck<T>> {

        private final Function<T, Boolean> getter;
        private final Serializer<BooleanCheck<T>> serializer;

        public Type(Function<T, Boolean> getter) {
            this.getter = getter;
            this.serializer = Serializer.BOOLEAN.fieldOf("value").flatMap(chk -> chk.value, bool -> new BooleanCheck<>(this, bool));
        }

        @Override
        public Serializer<BooleanCheck<T>> serializer() {
            return serializer;
        }
    }
}
