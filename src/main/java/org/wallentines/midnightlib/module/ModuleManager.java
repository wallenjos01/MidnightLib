package org.wallentines.midnightlib.module;

import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.event.Event;
import org.wallentines.midnightlib.event.HandlerList;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@SuppressWarnings("unused")
public class ModuleManager<T, M extends Module<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger("ModuleManager");

    private final String defaultNamespace;

    private final Registry<M> loaded;
    private final HashMap<Class<? extends M>, Identifier> idsByClass = new HashMap<>();
    private final HashMap<Identifier, Set<Identifier>> dependents = new HashMap<>();

    public final HandlerList<ModuleEvent> onLoad = new HandlerList<>();
    public final HandlerList<ModuleEvent> onUnload = new HandlerList<>();


    public ModuleManager() {
        this("midnight");
    }

    public ModuleManager(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        this.loaded = new Registry<>(defaultNamespace);
    }

    public int loadAll(ConfigSection section, T data, Registry<ModuleInfo<T, M>> registry) {

        if(loaded.getSize() > 0) {
            unloadAll();
        }

        section.fill(generateConfig(registry));

        int count = 0;
        for(String key : section.getKeys()) {

            if(!section.hasSection(key)) continue;

            Identifier id = Identifier.parseOrDefault(key, defaultNamespace);

            ModuleInfo<T, M> info = registry.get(id);
            if(info == null) {
                LOGGER.warn("Unknown module: " + id + " requested. Skipping...");
                continue;
            }

            count += loadWithDependencies(info, section, data, registry, new HashSet<>());
        }

        return count;
    }

    private int loadWithDependencies(ModuleInfo<T, M> info, ConfigSection config, T data, Registry<ModuleInfo<T, M>> registry, Collection<ModuleInfo<T, M>> loading) {

        if(loaded.get(info.getId()) != null) return 0;

        if(loading.contains(info)) {
            LOGGER.warn("Detected cyclical dependency while loading module " + info.getId());
            return 0;
        }

        loading.add(info);

        int count = 0;
        for(Identifier dep : info.getDependencies()) {

            ModuleInfo<T, M> depend = registry.get(dep);
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
    public boolean loadModule(ModuleInfo<T, M> info, T data, ConfigSection section) {

        M module = info.create();
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

                Event.unregisterAll(module);
                module.disable();
                return false;
            }
        } catch (Exception ex) {

            LOGGER.warn("An error occurred while attempting to initialize module with ID " + id + "!", ex);

            return false;
        }

        loaded.register(id, module);
        idsByClass.put((Class<M>) loaded.getClass(), id);

        onLoad.invoke(new ModuleEvent(module, id));

        return true;
    }

    public <O extends M> O getModule(Class<O> clazz) {

        Identifier id = getIdByClass(clazz);
        return id == null ? null : clazz.cast(loaded.get(id));
    }

    public M getModuleById(Identifier id) {

        return loaded.get(id);
    }

    public boolean isModuleLoaded(Identifier id) {

        return loaded.hasKey(id);
    }

    public Collection<Identifier> getLoadedModuleIds() {

        return loaded.getIds();
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

    public void unloadModule(Identifier moduleId) {

        M mod = loaded.get(moduleId);
        if(mod == null) return;

        unloadWithDependents(mod, moduleId);

        loaded.remove(moduleId);
    }

    public void unloadAll() {

        List<Identifier> ids = new ArrayList<>(loaded.getIds());
        for(Identifier id : ids) {

            if(!loaded.hasKey(id)) continue;
            unloadWithDependents(loaded.get(id), id);
        }

        loaded.clear();
    }

    public void reloadModule(Identifier moduleId, ConfigSection config, T data, ModuleInfo<T, M> info) {

        unloadModule(moduleId);
        loadModule(info, data, config);
    }

    public void reloadAll(ConfigSection config, T data, Registry<ModuleInfo<T, M>> reg) {

        unloadAll();
        loadAll(config, data, reg);
    }

    public <O extends M> Identifier getModuleId(Class<O> clazz) {

        return getIdByClass(clazz);
    }

    public Identifier getModuleId(M mod) {

        return loaded.getId(mod);
    }

    private <O extends M> Identifier getIdByClass(Class<O> clazz) {

        return idsByClass.computeIfAbsent(clazz, k -> {
            for(M mod : loaded) {
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

    public static <T, M extends Module<T>> ConfigSection generateConfig(Registry<ModuleInfo<T, M>> reg) {

        ConfigSection out = new ConfigSection();
        for(ModuleInfo<T, M> info : reg) {

            ConfigSection conf = info.getDefaultConfig();
            if(!conf.has("enabled")) conf.set("enabled", true);
            out.set(info.getId().toString(), conf);
        }

        return out;
    }

    public class ModuleEvent {
        private final M module;
        private final Identifier id;

        public ModuleEvent(M module, Identifier id) {
            this.module = module;
            this.id = id;
        }

        public M getModule() {
            return module;
        }

        public Identifier getId() {
            return id;
        }
    }

}
