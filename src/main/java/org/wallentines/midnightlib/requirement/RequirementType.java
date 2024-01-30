package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

public interface RequirementType<T> {

    <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value, boolean invert);

}
