package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.mdcfg.registry.Registry;

public interface Check<T> {

    boolean check(T data);

    CheckType<T, ?> type();

    static <T> Serializer<Check<T>> serializer(Registry<?, CheckType<T, ?>> registry) {
        return registry.byIdSerializer().fieldOf("type").dispatch(CheckType::serializer, Check::type);
    }

}
