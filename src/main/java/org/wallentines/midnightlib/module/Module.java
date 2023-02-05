package org.wallentines.midnightlib.module;

import org.wallentines.mdcfg.ConfigSection;

public interface Module<T> {

    boolean initialize(ConfigSection section, T data);

    default void disable() { }

}
