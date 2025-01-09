package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.Serializer;

public interface CheckType<T, C extends Check<T>> {

    Serializer<C> serializer();

}
