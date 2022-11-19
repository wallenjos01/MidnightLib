package org.wallentines.midnightlib.module;

import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModuleInfo<T> {

    private final Supplier<Module<T>> get;
    private final Identifier id;
    private final ConfigSection defaultConfig;

    private final List<Identifier> dependencies = new ArrayList<>();

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

    public List<Identifier> getDependencies() {
        return dependencies;
    }

    public ModuleInfo<T> dependsOn(Identifier id) {
        dependencies.add(id);
        return this;
    }
}
