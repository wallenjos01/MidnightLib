package org.wallentines.midnightlib.module;

import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ModuleInfo<T, M extends Module<T>> {

    private final Supplier<M> get;
    private final Identifier id;
    private final ConfigSection defaultConfig;

    private final List<Identifier> dependencies = new ArrayList<>();

    public ModuleInfo(Supplier<M> get, Identifier id, ConfigSection defaultConfig) {
        this.get = get;
        this.id = id;
        this.defaultConfig = defaultConfig;
    }

    public M create() {
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

    public ModuleInfo<T, M> dependsOn(Identifier id) {
        dependencies.add(id);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleInfo<?, ?> that = (ModuleInfo<?, ?>) o;
        return Objects.equals(get, that.get) && Objects.equals(id, that.id) && Objects.equals(defaultConfig, that.defaultConfig) && Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(get, id, defaultConfig, dependencies);
    }

    @Override
    public String toString() {
        return "ModuleInfo{" +
                "get=" + get +
                ", id=" + id +
                ", defaultConfig=" + defaultConfig +
                ", dependencies=" + dependencies +
                '}';
    }
}
