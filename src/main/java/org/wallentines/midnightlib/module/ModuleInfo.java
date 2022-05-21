package org.wallentines.midnightlib.module;

import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.function.Supplier;

public class ModuleInfo<T> {

    private final Supplier<Module<T>> get;
    private final Identifier id;
    private final ConfigSection defaultConfig;

    public ModuleInfo(Supplier<Module<T>> get, Identifier id, ConfigSection defaultConfig) {
        this.get = get;
        this.id = id;
        this.defaultConfig = defaultConfig;
    }

    public Module<T> create() {
        return get.get();
    }

    public Identifier getId() {
        return id;
    }

    public ConfigSection getDefaultConfig() {
        return defaultConfig;
    }
}
