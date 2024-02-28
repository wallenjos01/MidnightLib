package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

public interface CheckType<T> {

    <O> SerializeResult<Check<T>> deserialize(SerializeContext<O> context, O value);

}
