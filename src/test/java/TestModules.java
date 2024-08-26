import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.midnightlib.module.Module;
import org.wallentines.midnightlib.module.ModuleInfo;
import org.wallentines.midnightlib.module.ModuleManager;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestModules {

    private static class TestModule implements Module<Void> {
        final AtomicBoolean initialized;

        public TestModule(AtomicBoolean initialized) {
            this.initialized = initialized;
        }

        @Override
        public boolean initialize(ConfigSection config, Void data) {
            Assertions.assertTrue(config.getBoolean("enabled"));
            Assertions.assertTrue(config.getBoolean("default"));
            Assertions.assertEquals("Hello", config.getString("added"));
            initialized.set(true);
            return true;
        }
    }

    private static class TestModule2 implements Module<Void> {
        final AtomicBoolean initialized;

        public TestModule2(AtomicBoolean initialized) {
            this.initialized = initialized;
        }

        @Override
        public boolean initialize(ConfigSection config, Void data) {
            Assertions.assertTrue(config.getBoolean("enabled"));
            Assertions.assertTrue(config.getBoolean("default"));
            Assertions.assertEquals("Hello2", config.getString("added"));
            initialized.set(true);
            return true;
        }
    }

    private static class TestModule3 implements Module<String> {

        @Override
        public boolean initialize(ConfigSection config, String data) {
            Assertions.assertEquals("Fail", data);
            return false;
        }
    }

    @Test
    public void testModules() {

        Identifier id = new Identifier("midnight", "test");

        final AtomicBoolean initialized = new AtomicBoolean(false);
        ModuleInfo<Void, Module<Void>> info = new ModuleInfo<>(() -> new TestModule(initialized), id, new ConfigSection().with("default", true));

        Registry<Identifier, ModuleInfo<Void, Module<Void>>> reg = Registry.create("midnight");
        reg.register(id, info);

        ModuleManager<Void, Module<Void>> manager = new ModuleManager<>();

        ConfigSection defaults = ModuleManager.generateConfig(reg);
        defaults.getSection("midnight:test").set("added", "Hello");

        int loaded = manager.loadAll(defaults, null, reg);
        Assertions.assertEquals(1, loaded);
        Assertions.assertTrue(initialized.get());

        Assertions.assertNotNull(manager.getModule(TestModule.class));
        Assertions.assertNotNull(manager.getModuleById(id));

        Assertions.assertEquals(id, manager.getModuleId(TestModule.class));
        Assertions.assertTrue(manager.isModuleLoaded(id));
        Assertions.assertEquals(1, manager.getLoadedModuleIds().size());
        Assertions.assertEquals(1, manager.getCount());
        Assertions.assertEquals(id, manager.getLoadedModuleIds().iterator().next());

        TestModule mod = manager.getModule(TestModule.class);
        Assertions.assertEquals(id, manager.getModuleId(mod));

        // Reloading
        initialized.set(false);
        manager.reloadModule(id, defaults.getSection("midnight:test"), null, info);

        Assertions.assertTrue(initialized.get());

        initialized.set(false);
        manager.reloadAll(defaults, null, reg);

        Assertions.assertTrue(initialized.get());

    }

    @Test
    public void testEvents() {

        Identifier id = new Identifier("midnight", "test");

        final AtomicBoolean initialized = new AtomicBoolean(false);
        final AtomicBoolean eventLoaded = new AtomicBoolean(false);
        final AtomicBoolean eventUnloaded = new AtomicBoolean(false);
        ModuleInfo<Void, Module<Void>> info = new ModuleInfo<>(() -> new TestModule(initialized), id, new ConfigSection().with("default", true));

        Registry<Identifier, ModuleInfo<Void, Module<Void>>> reg = Registry.create("midnight");
        reg.register(id, info);

        ModuleManager<Void, Module<Void>> manager = new ModuleManager<>();
        manager.onLoad.register(this, ev -> {
            Assertions.assertEquals(TestModule.class, ev.getModule().getClass());
            Assertions.assertEquals(id, ev.getId());
            eventLoaded.set(true);
        });
        manager.onUnload.register(this, ev -> {
            Assertions.assertEquals(TestModule.class, ev.getModule().getClass());
            Assertions.assertEquals(id, ev.getId());
            eventUnloaded.set(true);
        });

        ConfigSection defaults = ModuleManager.generateConfig(reg);
        defaults.getSection("midnight:test").set("added", "Hello");

        int loaded = manager.loadAll(defaults, null, reg);
        Assertions.assertEquals(1, loaded);
        Assertions.assertTrue(initialized.get());
        Assertions.assertTrue(eventLoaded.get());
        Assertions.assertFalse(eventUnloaded.get());

        manager.unloadAll();

        Assertions.assertTrue(eventLoaded.get());
        Assertions.assertTrue(eventUnloaded.get());

    }

    @Test
    public void testDependencies() {

        Identifier id = new Identifier("midnight", "test");
        Identifier id2 = new Identifier("midnight", "test2");

        final AtomicBoolean initialized = new AtomicBoolean(false);
        final AtomicBoolean initialized2 = new AtomicBoolean(false);

        ModuleInfo<Void, Module<Void>> info = new ModuleInfo<>(() -> new TestModule(initialized), id, new ConfigSection().with("default", true));
        ModuleInfo<Void, Module<Void>> info2 = new ModuleInfo<Void, Module<Void>>(() -> new TestModule2(initialized2), id2, new ConfigSection().with("default", true)).dependsOn(id);

        Registry<Identifier, ModuleInfo<Void, Module<Void>>> reg = Registry.create("midnight");
        reg.register(id2, info2);
        reg.register(id, info);

        ModuleManager<Void, Module<Void>> manager = new ModuleManager<>();

        ConfigSection defaults = ModuleManager.generateConfig(reg);
        defaults.getSection("midnight:test").set("added", "Hello");
        defaults.getSection("midnight:test2").set("added", "Hello2");

        int loaded = manager.loadAll(defaults, null, reg);
        Assertions.assertEquals(2, loaded);
        Assertions.assertTrue(initialized.get());

        Assertions.assertNotNull(manager.getModule(TestModule.class));
        Assertions.assertNotNull(manager.getModule(TestModule2.class));
        Assertions.assertNotNull(manager.getModuleById(id));
        Assertions.assertNotNull(manager.getModuleById(id2));

        Assertions.assertEquals(id, manager.getModuleId(TestModule.class));
        Assertions.assertEquals(id2, manager.getModuleId(TestModule2.class));
        Assertions.assertTrue(manager.isModuleLoaded(id));
        Assertions.assertTrue(manager.isModuleLoaded(id2));
        Assertions.assertEquals(2, manager.getLoadedModuleIds().size());
        Assertions.assertEquals(2, manager.getCount());

        manager.unloadModule(id);

        Assertions.assertEquals(0, manager.getCount());

    }


    @Test
    public void testFail() {

        Identifier id = new Identifier("midnight", "test3");


        ModuleInfo<String, Module<String>> info = new ModuleInfo<>(TestModule3::new, id, new ConfigSection().with("default", true));

        Registry<Identifier, ModuleInfo<String, Module<String>>> reg = Registry.create("midnight");
        reg.register(id, info);

        ModuleManager<String, Module<String>> manager = new ModuleManager<>();

        ConfigSection defaults = ModuleManager.generateConfig(reg);

        int loaded = manager.loadAll(defaults, "Fail", reg);
        Assertions.assertEquals(0, loaded);

    }
}
