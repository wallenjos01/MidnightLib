package org.wallentines.midnightlib.module;

import org.wallentines.mdcfg.ConfigSection;

/**
 * An interface for a module loaded by a module manager
 * @param <T> The type of data to pass in during module initialization
 */
public interface Module<T> {

    /**
     * Initializes a modules according to the given config and data
     * @param config The module configuration
     * @param data The data
     * @return Whether initialization was successful
     */
    boolean initialize(ConfigSection config, T data);

    /**
     * Called when a module is disabled
     */
    default void disable() { }

}
