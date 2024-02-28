package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.function.Predicate;

public interface Check<T> {

    boolean check(T data);

    <O> SerializeResult<O> serialize(SerializeContext<O> context);

    static <T> Check<T> of(Predicate<T> predicate) {
        return new Check<T>() {
            @Override
            public boolean check(T data) {
                return predicate.test(data);
            }

            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
                return SerializeResult.failure("This check cannot be serialized!");
            }
        };
    }

}
