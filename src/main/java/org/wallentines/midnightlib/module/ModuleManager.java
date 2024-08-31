package org.wallentines.midnightlib.module;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wallentines.mdcfg.ConfigPrimitive;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.midnightlib.event.Event;
import org.wallentines.midnightlib.event.HandlerList;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;

import java.util.*;

/**
 * Loads, initializes, and manages modules
 * @param <T> The type of data modules expect
 * @param <M> The type of modules to load
 */
public class ModuleManager<T, M extends Module<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger("ModuleManager");

    private final Registry<Identifier, ModuleInfo<T, M>> registry;
    private final Registry<Identifier, M> loaded;
    private final T data;

    private final HashMap<Class<?>, Identifier> idsByClass = new HashMap<>();
    private final HashMap<Identifier, Set<Identifier>> dependents = new HashMap<>();

    /**
     * Invoked when a module is loaded
     */
    public final HandlerList<ModuleEvent> onLoad = new HandlerList<>();

    /**
     * Invoked when a module is unloaded
     */
    public final HandlerList<ModuleEvent> onUnload = new HandlerList<>();


    /**
     * Constructs a new module manager for the given modules and init data
     * @param modules The registry of module infos
     * @param data The data to pass into module initializers
     */
    public ModuleManager(Registry<Identifier, ModuleInfo<T, M>> modules, T data) {
        this(modules, data, "unknown");
    }

    /**
     * Constructs a new module manager for the given modules, init data, and default namespace
     * @param modules The registry of module infos
     * @param data The data to pass into module initializers
     * @param defaultNamespace The default namespace for modules
     */
    public ModuleManager(Registry<Identifier, ModuleInfo<T, M>> modules, T data, String defaultNamespace) {
        this.registry = modules;
        this.loaded = Registry.create(defaultNamespace);
        this.data = data;
    }

    /**
     * Gets the registry where module infos are stored for this manager
     * @return The module registry
     */
    public Registry<Identifier, ModuleInfo<T, M>> getRegistry() {
        return registry;
    }

    /**
     * Gets the data passed to each module initializer
     * @return The init data passed to module initializers
     */
    public T getData() {
        return data;
    }

    /**
     * Creates and initializes all modules from the given registry, reading the given config and passing in the given
     * data. All existing modules will be unloaded first.
     * @param config The module registry config (see {@link ModuleManager#generateConfig(Registry) generateConfig})
     * @return How many modules were loaded
     */
    public int loadAll(ConfigSection config) {

        if(loaded.getSize() > 0) {
            unloadAll();
        }

        config.fill(generateConfig(registry));

        List<ModuleInfo<T,M>> availableModules = new ArrayList<>();

        int count = 0;
        for(String key : config.getKeys()) {

            if(!config.hasSection(key)) continue;

            SerializeResult<ModuleInfo<T, M>> res = registry.byIdSerializer().deserialize(ConfigContext.INSTANCE, new ConfigPrimitive(key));
            if(!res.isComplete()) {
                LOGGER.warn("Unknown module: {} requested. Skipping...", key);
                continue;
            }

            if(config.getSection(key).getBoolean("enabled")) {
                availableModules.add(res.getOrThrow());
            }

        }

        for(ModuleInfo<T, M> info : availableModules) {
            count += loadWithDependencies(info, config, new HashSet<>());
        }

        return count;
    }

    /**
     * creates a module from the given module info and initializes it with the given data and configuration
     * @param id The ID of the module to load
     * @param config The module configuration
     * @return Whether loading was successful
     */
    @SuppressWarnings("unchecked")
    public boolean loadModule(Identifier id, ConfigSection config) {

        ModuleInfo<T, M> info = registry.get(id);
        if (info == null) {
            LOGGER.error("Could not find module with id {}", id);
            return false;
        }

        M module = info.create();

        if(idsByClass.containsKey(module.getClass())) {
            LOGGER.warn("Attempt to initialize two of the same module!");
            return false;
        }
        if(loaded.hasKey(id)) {
            LOGGER.warn("Attempt to initialize module with duplicate ID!");
            return false;
        }

        ConfigSection defaults = info.getDefaultConfig();
        if(defaults == null) defaults = new ConfigSection();

        config.fill(defaults);

        for(Identifier dep : info.getDependencies()) {
            if(!loaded.hasKey(dep)) {
                LOGGER.warn("One or more dependencies could not be found for module {}! [{}]", id, dep);
                return false;
            }
        }

        try {
            if(!module.initialize(config, data)) {
                LOGGER.warn("Unable to initialize module {}!", id);

                Event.unregisterAll(module);
                module.disable();
                return false;
            }
        } catch (Exception ex) {

            LOGGER.warn("An error occurred while attempting to initialize module with ID {}!", id, ex);

            return false;
        }

        loaded.register(id, module);
        idsByClass.put((Class<M>) loaded.getClass(), id);

        onLoad.invoke(new ModuleEvent(module, id));

        return true;
    }

    /**
     * Gets a module which is or inherits from the given class
     * @param clazz The class to look up
     * @return A module of that class, or null if none is found
     * @param <O> The type of module to lookup
     */
    @Nullable
    public <O> O getModule(Class<O> clazz) {

        Identifier id = getModuleId(clazz);
        return id == null ? null : clazz.cast(loaded.get(id));
    }

    /**
     * Gets the module info for the module with the given ID, or null
     * @param id The ID to lookup
     * @return A module info, or null if the module is not registered
     */
    @Nullable
    public ModuleInfo<T, M> getModuleInfo(Identifier id) {
        return registry.get(id);
    }

    /**
     * Gets a module with the given ID
     * @param id The ID to lookup
     * @return A module with that ID, or null if none is found
     */
    @Nullable
    public M getModuleById(Identifier id) {

        return loaded.get(id);
    }

    /**
     * Determines if a module with the given ID is loaded
     * @param id The ID to lookup
     * @return Whether that module is loaded
     */
    public boolean isModuleLoaded(Identifier id) {

        return loaded.hasKey(id);
    }

    /**
     * Gets a collection of IDs for all loaded modules
     * @return A collection of loaded module IDs
     */
    public Collection<Identifier> getLoadedModuleIds() {

        return loaded.getIds();
    }

    /**
     * Unloads a module with the given ID
     * @param moduleId The ID of the module to unload
     */
    public void unloadModule(Identifier moduleId) {

        M mod = loaded.get(moduleId);
        if(mod == null) return;

        unloadWithDependents(mod, moduleId);
        Collection<Class<?>> classes = new ArrayList<>();

        for(Map.Entry<Class<?>, Identifier> ent : idsByClass.entrySet()) {
            if(ent.getValue().equals(moduleId)) {
                classes.add(ent.getKey());
            }
        }
        for(Class<?> clazz : classes) {
            idsByClass.remove(clazz);
        }


        loaded.remove(moduleId);
    }

    /**
     * Unloads all modules
     */
    public void unloadAll() {

        List<Identifier> ids = new ArrayList<>(loaded.getIds());
        for(Identifier id : ids) {

            if(!loaded.hasKey(id)) continue;
            unloadWithDependents(loaded.get(id), id);
        }

        loaded.clear();
        idsByClass.clear();
    }

    /**
     * Reloads a module with the given ID
     * @param moduleId The module ID to look up
     * @param config The module config
     */
    public void reloadModule(Identifier moduleId, ConfigSection config) {

        unloadModule(moduleId);
        loadModule(moduleId, config);
    }

    /**
     * Reloads all modules
     * @param config The module registry config (see {@link ModuleManager#generateConfig(Registry) generateConfig})
     */
    public void reloadAll(ConfigSection config) {

        unloadAll();
        loadAll(config);
    }

    /**
     * Gets the ID of a loaded module with the given class
     * @param clazz The class to lookup
     * @return The ID of the loaded module of that class, or null of none is found
     * @param <O> The type of module
     */
    @Nullable
    public <O> Identifier getModuleId(Class<O> clazz) {

        return idsByClass.computeIfAbsent(clazz, k -> {
            for(M mod : loaded) {
                if(clazz == mod.getClass() || clazz.isAssignableFrom(mod.getClass())) {
                    return loaded.getId(mod);
                }
            }
            return null;
        });
    }

    /**
     * Gets the ID of a loaded module
     * @param mod The module itself
     * @return The module's ID
     */
    public Identifier getModuleId(M mod) {

        return loaded.getId(mod);
    }

    /**
     * Gets the number of loaded modules
     * @return The number of loaded modules
     */
    public int getCount() {

        return loaded.getSize();
    }


    /**
     * Generates a module registry config by reading the default configurations of all the module info in the given registry.
     * @param reg The registry to read
     * @return The default module registry config for the given registry
     * @param <T> The type of data modules expect during initialization
     * @param <M> The type of modules to load
     */
    public static <T, M extends Module<T>> ConfigSection generateConfig(Registry<Identifier, ModuleInfo<T, M>> reg) {

        ConfigSection out = new ConfigSection();
        for(ModuleInfo<T, M> info : reg) {

            ConfigSection conf = info.getDefaultConfig();
            if(!conf.has("enabled")) conf.set("enabled", true);
            out.set(info.getId().toString(), conf);
        }

        return out;
    }

    /**
     * An event fired when a module is loaded or unloaded
     */
    public class ModuleEvent {
        private final M module;
        private final Identifier id;

        /**
         * Constructs a new module event with the given module and ID
         * @param module The module
         * @param id The ID of the module
         */
        public ModuleEvent(M module, Identifier id) {
            this.module = module;
            this.id = id;
        }

        /**
         * Gets the module itself
         * @return The module
         */
        public M getModule() {
            return module;
        }

        /**
         * Gets the module's ID
         * @return The module's ID
         */
        public Identifier getId() {
            return id;
        }
    }


    private int loadWithDependencies(ModuleInfo<T, M> info, ConfigSection config, Collection<ModuleInfo<T, M>> loading) {

        if(loaded.get(info.getId()) != null) return 0;

        // Avoid infinite recursion if a module depends on itself by keeping track of which modules are currently in
        // the process of loading
        if(!loading.add(info)) {
            LOGGER.warn("Detected cyclical dependency while loading module {}", info.getId());
            return 0;
        }

        int count = 0;
        for(Identifier dep : info.getDependencies()) {

            ModuleInfo<T, M> depend = registry.get(dep);
            if(depend == null) {
                LOGGER.warn("One or more dependencies could not be loaded for module {}! [{}]", info.getId(), dep);
                return count;
            }

            dependents.computeIfAbsent(depend.getId(), k -> new HashSet<>()).add(info.getId());

            // Recursively load dependencies first
            count += loadWithDependencies(depend, config, loading);
        }

        if(loadModule(info.getId(), config.getSection(info.getId().toString()))) {
            count++;
        }

        // After a module is loaded, remove it from the running list of loading modules
        loading.remove(info);

        return count;
    }


    private void unloadWithDependents(M mod, Identifier moduleId) {

        if(dependents.containsKey(moduleId)) {
            for (Identifier id : dependents.get(moduleId)) {
                unloadModule(id);
            }
        }

        onUnload.invoke(new ModuleEvent(mod, moduleId));

        // Make sure this module does not try to handle events after it is disabled.
        Event.unregisterAll(mod);

        try {
            mod.disable();
        } catch (Exception ex) {
            LOGGER.warn("An exception occurred while disabling a module!", ex);
        }

        dependents.remove(moduleId);
    }

}
