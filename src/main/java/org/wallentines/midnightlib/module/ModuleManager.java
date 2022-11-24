package org.wallentines.midnightlib.module;

import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.event.Event;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ModuleManager<T> {

    private static final Logger LOGGER = LogManager.getLogger("ModuleManager");

    private final String defaultNamespace;

    private final Registry<Module<T>> loaded = new Registry<>();
    private final HashMap<Class<? extends Module<T>>, Identifier> idsByClass = new HashMap<>();
    private final HashMap<Identifier, Set<Identifier>> dependents = new HashMap<>();

    public ModuleManager() {
        this("midnight");
    }

    public ModuleManager(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public int loadAll(ConfigSection section, T data, Registry<ModuleInfo<T>> registry) {

        if(loaded.getSize() > 0) {
            unloadAll();
        }

        section.fill(generateConfig(registry));

        int count = 0;
        for(String key : section.getKeys()) {

            if(!section.has(key, ConfigSection.class)) continue;

            Identifier id = Identifier.parseOrDefault(key, defaultNamespace);

            ModuleInfo<T> info = registry.get(id);
            if(info == null) {
                LOGGER.warn("Unknown module: " + id + " requested. Skipping...");
                continue;
            }

            count += loadWithDependencies(info, section, data, registry, new HashSet<>());
        }

        return count;
    }

    private int loadWithDependencies(ModuleInfo<T> info, ConfigSection config, T data, Registry<ModuleInfo<T>> registry, Collection<ModuleInfo<T>> loading) {

        if(loaded.get(info.getId()) != null) return 0;

        if(loading.contains(info)) {
            LOGGER.warn("Detected cyclical dependency while loading module " + info.getId());
            return 0;
        }

        loading.add(info);

        int count = 0;
        for(Identifier dep : info.getDependencies()) {

            ModuleInfo<T> depend = registry.get(dep);
            if(depend == null) {
                LOGGER.warn("One or more dependencies could not be found for module " + info.getId() + "! [" + dep + "]");
                return count;
            }

            dependents.computeIfAbsent(depend.getId(), k -> new HashSet<>()).add(info.getId());

            count += loadWithDependencies(depend, config, data, registry, loading);
        }

        if(loadModule(info, data, config.getSection(info.getId().toString()))) {
            count++;
        }

        loading.remove(info);

        return count;
    }

    @SuppressWarnings("unchecked")
    public <M extends Module<T>> boolean loadModule(ModuleInfo<T> info, T data, ConfigSection section) {

        Module<T> module = info.create();
        Identifier id = info.getId();

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

        section.fill(defaults);

        if(!section.getBoolean("enabled")) return false;

        for(Identifier dep : info.getDependencies()) {
            if(!loaded.hasKey(dep)) {
                LOGGER.warn("One or more dependencies could not be found for module " + id + "! [" + dep + "]");
                return false;
            }
        }

        try {
            if(!module.initialize(section, data)) {
                LOGGER.warn("Unable to initialize module " + id + "!");
                return false;
            }
        } catch (Exception ex) {

            LOGGER.warn("An error occurred while attempting to initialize module with ID " + id + "!");
            ex.printStackTrace();

            return false;
        }

        loaded.register(id, module);
        idsByClass.put((Class<M>) loaded.getClass(), id);

        return true;
    }

    public <M extends Module<T>> M getModule(Class<M> clazz) {

        Identifier id = getIdByClass(clazz);
        return id == null ? null : clazz.cast(loaded.get(id));
    }

    public Module<T> getModuleById(Identifier id) {

        return loaded.get(id);
    }

    public boolean isModuleLoaded(Identifier id) {

        return loaded.hasKey(id);
    }

    public Collection<Identifier> getLoadedModuleIds() {

        return loaded.getIds();
    }

    private void unloadWithDependents(Module<T> mod, Identifier moduleId) {

        if(dependents.containsKey(moduleId)) {
            for (Identifier id : dependents.get(moduleId)) {
                unloadModule(id);
            }
        }

        // Make sure this module does not try to handle events after it is disabled.
        Event.unregisterAll(mod);
        try {
            mod.disable();
        } catch (Exception ex) {
            LOGGER.warn("An exception occurred while disabling a module!");
            ex.printStackTrace();
        }

        dependents.remove(moduleId);
    }

    public void unloadModule(Identifier moduleId) {

        Module<T> mod = loaded.get(moduleId);
        if(mod == null) return;

        unloadWithDependents(mod, moduleId);

        loaded.removeById(moduleId);
    }

    public void unloadAll() {

        List<Identifier> ids = new ArrayList<>(loaded.getIds());
        for(Identifier id : ids) {

            if(!loaded.hasKey(id)) continue;
            unloadWithDependents(loaded.get(id), id);
        }

        loaded.clear();
    }

    public void reloadModule(Identifier moduleId, ConfigSection config, T data, ModuleInfo<T> info) {

        unloadModule(moduleId);
        loadModule(info, data, config);
    }

    public void reloadAll(ConfigSection config, T data, Registry<ModuleInfo<T>> reg) {

        unloadAll();
        loadAll(config, data, reg);
    }

    public <M extends Module<T>> Identifier getModuleId(Class<M> clazz) {

        return getIdByClass(clazz);
    }

    public Identifier getModuleId(Module<T> mod) {

        return loaded.getId(mod);
    }

    private <M extends Module<T>> Identifier getIdByClass(Class<M> clazz) {

        return idsByClass.computeIfAbsent(clazz, k -> {
            for(Module<T> mod : loaded) {
                if(clazz == mod.getClass() || clazz.isAssignableFrom(mod.getClass())) {
                    return loaded.getId(mod);
                }
            }
            return null;
        });
    }

    public int getCount() {

        return loaded.getSize();
    }

    public static <D> ConfigSection generateConfig(Registry<ModuleInfo<D>> reg) {

        ConfigSection out = new ConfigSection();
        for(ModuleInfo<D> info : reg) {

            ConfigSection conf = info.getDefaultConfig();
            if(!conf.has("enabled")) conf.set("enabled", true);
            out.set(info.getId().toString(), conf);
        }

        return out;
    }

}
