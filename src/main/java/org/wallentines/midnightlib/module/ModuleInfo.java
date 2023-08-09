package org.wallentines.midnightlib.module;

import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.registry.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stores information about a module, so it can be created by a module manager
 * @param <T> The type of data the module expects
 * @param <M> The type of module
 */
public class ModuleInfo<T, M extends Module<T>> {

    private final Supplier<M> get;
    private final Identifier id;
    private final ConfigSection defaultConfig;

    private final List<Identifier> dependencies = new ArrayList<>();

    /**
     * Creates a ModuleInfo object with the given constructor, ID, and default configuration
     * @param get The constructor by which to build modules
     * @param id The ID of the module
     * @param defaultConfig The default configuration for the module
     */
    public ModuleInfo(Supplier<M> get, Identifier id, ConfigSection defaultConfig) {
        this.get = get;
        this.id = id;
        this.defaultConfig = defaultConfig;
    }

    /**
     * Creates a new module
     * @return A new module
     */
    public M create() {
        return get.get();
    }

    /**
     * Gets the ID of the module type
     * @return The module's type ID
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Gets the default configuration of the module
     * @return The module's default configuration
     */
    public ConfigSection getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * Gets the IDs of the modules which this module depends on
     * @return The dependencies
     */
    public List<Identifier> getDependencies() {
        return dependencies;
    }

    /**
     * Adds a dependency to the module
     * @param id The dependency to add
     * @return A reference to self
     */
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
