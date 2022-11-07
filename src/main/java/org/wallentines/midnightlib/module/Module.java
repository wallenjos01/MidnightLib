package org.wallentines.midnightlib.module;

import org.wallentines.midnightlib.config.ConfigSection;

public interface Module<T> {

    boolean initialize(ConfigSection section, T data);

    @Deprecated
    default void reload(ConfigSection config) { }

    default void disable() { }

}
