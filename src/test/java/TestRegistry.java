import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.registry.FrozenRegistry;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.registry.StringRegistry;

public class TestRegistry {

    @Test
    public void testIdentifier() {

        Identifier id = new Identifier("namespace", "path");
        Assertions.assertEquals("namespace", id.getNamespace());
        Assertions.assertEquals("path", id.getPath());
        Assertions.assertEquals("namespace:path", id.toString());

        String unparsed = "test:id";
        Identifier parsed = Identifier.parse(unparsed);
        Assertions.assertEquals("test", parsed.getNamespace());
        Assertions.assertEquals("id", parsed.getPath());

        unparsed = "namespaceless";
        parsed = Identifier.parseOrDefault(unparsed, "test");
        Assertions.assertEquals("test", parsed.getNamespace());
        Assertions.assertEquals("namespaceless", parsed.getPath());

        unparsed = "test:id";
        parsed = Identifier.parseOrDefault(unparsed, "default");
        Assertions.assertEquals("test", parsed.getNamespace());
        Assertions.assertEquals("id", parsed.getPath());

    }

    @Test
    public void testRegistryBase() {

        StringRegistry<Integer> registry = new StringRegistry<>();
        Assertions.assertEquals(0, registry.getSize());

        int registered = registry.register("key1", 1);
        Assertions.assertEquals(1, registry.getSize());
        Assertions.assertEquals(1, registered);
        Assertions.assertEquals(1, registry.get("key1"));
        Assertions.assertTrue(registry.hasKey("key1"));

        registry.register("key2", 2);
        Assertions.assertEquals(2, registry.getSize());
        Assertions.assertEquals(1, registry.get("key1"));
        Assertions.assertEquals(2, registry.get("key2"));
        Assertions.assertTrue(registry.hasKey("key1"));
        Assertions.assertTrue(registry.hasKey("key2"));

        Assertions.assertEquals(1, registry.indexOf(2));
        Assertions.assertEquals(2, registry.valueAtIndex(1));
        Assertions.assertEquals("key2", registry.idAtIndex(1));

        Assertions.assertEquals(2, registry.removeValue(2));
        Assertions.assertEquals(1, registry.getSize());

        Assertions.assertEquals(1, registry.remove("key1"));
        Assertions.assertEquals(0, registry.getSize());

        registry.register("key3", 3);
        registry.register("key4", 4);
        registry.register("key5", 5);

        Assertions.assertEquals(3, registry.getSize());
        Assertions.assertEquals(4, registry.removeAtIndex(1));

        Assertions.assertTrue(registry.isRegistered(3));
        Assertions.assertTrue(registry.isRegistered(5));
        Assertions.assertFalse(registry.isRegistered(4));

        Assertions.assertTrue(registry.contains("key3"));
        Assertions.assertTrue(registry.contains("key5"));
        Assertions.assertFalse(registry.contains("key4"));

        registry.clear();
        Assertions.assertEquals(0, registry.getSize());

    }

    @Test
    public void testRegistry() {

        Registry<Integer> registry = new Registry<>("test");
        Assertions.assertEquals(0, registry.getSize());
        Assertions.assertEquals("test", registry.getDefaultNamespace());

        Identifier key1 = new Identifier("test", "key1");
        registry.register(key1, 1);
        Assertions.assertEquals(1, registry.getSize());
        Assertions.assertEquals(1, registry.get(key1));

        registry.register(new Identifier("test2", "key2"), 2);
        Assertions.assertEquals(2, registry.getSize());
        Assertions.assertEquals(1, registry.get(key1));
        Assertions.assertEquals(2, registry.get(new Identifier("test2", "key2")));
    }

    @Test
    public void testRegistryFreeze() {

        Registry<Integer> registry = new Registry<>("test");
        Assertions.assertEquals(0, registry.getSize());
        Assertions.assertEquals("test", registry.getDefaultNamespace());

        Identifier key1 = new Identifier("test", "key1");
        registry.register(key1, 1);
        Assertions.assertEquals(1, registry.getSize());
        Assertions.assertEquals(1, registry.get(key1));

        registry.register(new Identifier("test2", "key2"), 2);
        Assertions.assertEquals(2, registry.getSize());
        Assertions.assertEquals(1, registry.get(key1));
        Assertions.assertEquals(2, registry.get(new Identifier("test2", "key2")));


        FrozenRegistry<Identifier, Integer> frozen = registry.freeze();

        Assertions.assertEquals(2, frozen.getSize());
        Assertions.assertEquals(1, frozen.get(key1));
        Assertions.assertEquals(2, frozen.get(new Identifier("test2", "key2")));

        boolean caught = false;
        try {
            frozen.register(new Identifier("test", "wont_work"), 3);
        } catch (IllegalStateException ex) {
            caught = true;
        }

        Assertions.assertEquals(2, frozen.getSize());
        Assertions.assertTrue(caught);

    }

}
