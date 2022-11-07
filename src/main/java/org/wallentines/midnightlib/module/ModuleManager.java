package org.wallentines.midnightlib.module;

import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleManager<T> {


    private static final Logger LOGGER = LogManager.getLogger("ModuleManager");

    private final String defaultNamespace;
    private final List<Module<T>> loaded = new ArrayList<>();
    private final HashMap<Class<? extends Module<T>>, Integer> indicesByClass = new HashMap<>();
    private final HashMap<Identifier, Integer> indicesById = new HashMap<>();

    public ModuleManager() {
        this("midnight");
    }

    public ModuleManager(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public int loadAll(ConfigSection section, T data, Registry<ModuleInfo<T>> registry) {

        if(loaded.size() > 0) {
            for (Module<T> mod : loaded) {
                mod.disable();
            }

            loaded.clear();
            indicesByClass.clear();
            indicesById.clear();
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

            if(loadModule(info, data, section.getSection(key))) {
                count++;
            }
        }

        return count;
    }

    @SuppressWarnings("unchecked")
    public <M extends Module<T>> boolean loadModule(ModuleInfo<T> info, T data, ConfigSection section) {

        Module<T> module = info.create();
        Identifier id = info.getId();

        if(indicesByClass.containsKey(module.getClass())) {
            LOGGER.warn("Attempt to initialize two of the same module!");
            return false;
        }
        if(indicesById.containsKey(id)) {
            LOGGER.warn("Attempt to initialize module with duplicate ID!");
            return false;
        }

        ConfigSection defaults = info.getDefaultConfig();
        if(defaults == null) defaults = new ConfigSection();

        section.fill(defaults);

        if(!section.getBoolean("enabled")) return false;

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

        int index = loaded.size();

        loaded.add(module);
        indicesByClass.put((Class<M>) loaded.getClass(), index);
        indicesById.put(id, index);

        return true;
    }

    @SuppressWarnings("unchecked")
   public <M extends Module<T>> M getModule(Class<M> clazz) {

        Integer index = indicesByClass.get(clazz);
        if(index != null) return (M) loaded.get(index);

        for(int i = 0 ; i < loaded.size() ; i++) {

            Module<T> mod = loaded.get(i);
            if(clazz.isAssignableFrom(mod.getClass())) {

                indicesByClass.put(clazz, i);
                return (M) mod;
            }
        }

        return null;
    }

    public Module<T> getModuleById(Identifier id) {

        Integer index = indicesById.get(id);
        if(index == null) return null;

        return loaded.get(index);
    }

    public boolean isModuleLoaded(Identifier id) {

        return indicesById.containsKey(id);
    }

    public Iterable<Identifier> getLoadedModuleIds() {
        return indicesById.keySet();
    }

    public void unloadModule(Identifier moduleId) {

        int index = indicesById.get(moduleId);
        if(index == -1) return;

        Module<T> mod = loaded.get(index);

        try {
            mod.disable();
        } catch (Exception ex) {
            LOGGER.warn("An exception occurred while disabling a module!");
            ex.printStackTrace();
        }

        indicesById.remove(moduleId);
        indicesByClass.remove(mod.getClass());

        loaded.set(index, (section, data) -> true);
    }

    public void unloadAll() {

        List<Identifier> ids = new ArrayList<>(indicesById.keySet());
        for(Identifier id : ids) {
            unloadModule(id);
        }
    }

    public void reloadModule(Identifier moduleId, ConfigSection config, T data, ModuleInfo<T> info) {

        unloadModule(moduleId);
        loadModule(info, data, config);
    }

    public void reloadAll(ConfigSection config, T data, Registry<ModuleInfo<T>> reg) {

        unloadAll();
        loadAll(config, data, reg);
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
