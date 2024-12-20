import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigList;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.requirement.*;

import java.util.function.Function;


public class TestRequirements {

    @Test
    public void testBoolean() {

        Registry<String, CheckType<Boolean, ?>> types = Registry.createStringRegistry();
        types.register("boolean", new BooleanCheck.Type<>(Function.identity()));

        Serializer<Requirement<Boolean>> ser = Requirement.serializer(types);

        Requirement<Boolean> req = ser.deserialize(ConfigContext.INSTANCE, new ConfigSection().with("type", "boolean").with("value", true)).getOrThrow();

        Assertions.assertTrue(req.check(true));
        Assertions.assertFalse(req.check(false));
    }

    @Test
    public void testString() {

        Registry<String, CheckType<String, ?>> types = Registry.createStringRegistry();
        types.register("string", new StringCheck.Type<>(Function.identity()));

        Serializer<Requirement<String>> ser = Requirement.serializer(types);

        Requirement<String> req = ser.deserialize(ConfigContext.INSTANCE, new ConfigSection().with("type", "string").with("value", "test")).getOrThrow();

        Assertions.assertTrue(req.check("test"));
        Assertions.assertFalse(req.check("test2"));

        req = ser.deserialize(ConfigContext.INSTANCE, new ConfigSection().with("type", "string").with("value", new ConfigList().append("test").append("test2"))).getOrThrow();

        Assertions.assertTrue(req.check("test"));
        Assertions.assertTrue(req.check("test2"));
    }

    @Test
    public void testNumber() {

        Registry<String, CheckType<Number, ?>> types = Registry.createStringRegistry();
        types.register("number", new NumberCheck.Type<>(Number::intValue, Range.INTEGER));

        Serializer<Requirement<Number>> ser = Requirement.serializer(types);

        Requirement<Number> req = ser.deserialize(ConfigContext.INSTANCE, new ConfigSection().with("type", "number").with("value", 1)).getOrThrow();

        Assertions.assertTrue(req.check(1));
        Assertions.assertFalse(req.check(2));

        req = ser.deserialize(ConfigContext.INSTANCE, new ConfigSection().with("type", "number").with("value", new ConfigList().append(1).append(2))).getOrThrow();

        Assertions.assertTrue(req.check(1));
        Assertions.assertTrue(req.check(2));

        req = ser.deserialize(ConfigContext.INSTANCE, new ConfigSection().with("type", "number").with("value", ">1")).getOrThrow();

        Assertions.assertFalse(req.check(1));
        Assertions.assertTrue(req.check(2));
    }

    @Test
    public void testComposite() {

        Registry<String, CheckType<String, ?>> types = Registry.createStringRegistry();
        types.register("string", new StringCheck.Type<>(Function.identity()));
        types.register("composite", new CompositeCheck.Type<>(types));

        Serializer<Requirement<String>> ser = Requirement.serializer(types);

        Requirement<String> req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.exactly(1))).getOrThrow();

        Assertions.assertTrue(req.check("test"));
        Assertions.assertTrue(req.check("test2"));
        Assertions.assertFalse(req.check("test3"));


        req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.exactly(2))).getOrThrow();

        Assertions.assertFalse(req.check("test"));
        Assertions.assertFalse(req.check("test2"));
        Assertions.assertFalse(req.check("test3"));


        req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.all())).getOrThrow();

        Assertions.assertFalse(req.check("test"));
        Assertions.assertFalse(req.check("test2"));
        Assertions.assertFalse(req.check("test3"));


        req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.exactly(0))).getOrThrow();

        Assertions.assertFalse(req.check("test"));
        Assertions.assertFalse(req.check("test2"));
        Assertions.assertTrue(req.check("test3"));


        req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.atLeast(1))).getOrThrow();

        Assertions.assertTrue(req.check("test"));
        Assertions.assertTrue(req.check("test2"));
        Assertions.assertFalse(req.check("test3"));


        req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.greaterThan(0))).getOrThrow();

        Assertions.assertTrue(req.check("test"));
        Assertions.assertTrue(req.check("test2"));
        Assertions.assertFalse(req.check("test3"));


        req = ser.deserialize(ConfigContext.INSTANCE, createConfig(Range.all())).getOrThrow();

        Assertions.assertFalse(req.check("test"));
        Assertions.assertFalse(req.check("test2"));
        Assertions.assertFalse(req.check("test3"));
    }

    private static ConfigSection createConfig(Range<Integer> range) {
        return new ConfigSection()
                .with("type", "composite")
                .with("values", new ConfigList()
                        .append(new ConfigSection()
                                .with("type", "string")
                                .with("value", "test")
                        )
                        .append(new ConfigSection()
                                .with("type", "string")
                                .with("value", "test2")
                        )
                )
                .with("count", range, Range.INTEGER);
    }
}
