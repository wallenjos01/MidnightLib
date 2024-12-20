package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.mdcfg.TypeReference;

public interface CheckType<T, C extends Check<T>> {

    TypeReference<C> type();

    Serializer<C> serializer();

}
