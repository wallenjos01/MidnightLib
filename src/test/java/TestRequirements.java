import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.requirement.Check;
import org.wallentines.midnightlib.requirement.CheckType;
import org.wallentines.midnightlib.requirement.Requirement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestRequirements {


    private static class MustEqual implements Check<String> {

        private final String data;

        public MustEqual(String value) {
            this.data = value;
        }

        @Override
        public boolean check(String data) {
            return Objects.equals(data, this.data);
        }

        @Override
        public <O> SerializeResult<O> serialize(SerializeContext<O> context) {
            return Serializer.STRING.fieldOf("value").serialize(context, data);
        }

        public static final CheckType<String> TYPE = new CheckType<String>() {
            @Override
            public <O> SerializeResult<Check<String>> deserialize(SerializeContext<O> context, O value) {
                return Serializer.STRING.fieldOf("value").deserialize(context, value).flatMap(MustEqual::new);
            }
        };

    }

    @Test
    public void testMultiRequirement() {

        List<Requirement<String, CheckType<String>>> lst = getRequirements();

        Requirement<String, CheckType<String>> req = Requirement.composite(Range.atLeast(1), lst);

        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = Requirement.composite(Range.all(), lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = Requirement.composite(Range.exactly(1), lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = Requirement.composite(Range.atLeast(1), lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = Requirement.composite(Range.atMost(2), lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertTrue(req.check("World"));

        req = Requirement.composite(Range.closedInterval(2,3), lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));
    }

    private static List<Requirement<String, CheckType<String>>> getRequirements() {

       return Arrays.asList(
               Requirement.simple(new MustEqual("Hello")),
               Requirement.simple(new MustEqual("Hello2")),
               Requirement.simple(new MustEqual("Hello")),
               Requirement.simple(new MustEqual("Hello3")),
               Requirement.simple(new MustEqual("Hello3")),
               Requirement.simple(new MustEqual("Hello3"))
       );
    }

    @Test
    public void testRegistry() {

        Registry<CheckType<String>> reg = Requirement.defaultRegistry("test");
        reg.register("must_equal", MustEqual.TYPE);

        Serializer<Requirement<String, CheckType<String>>> ser = Requirement.serializer(reg);

        ConfigSection serialized = new ConfigSection()
                .with("type", "test:must_equal")
                .with("value", "Hello")
                .with("invert", true);

        Requirement<String, CheckType<String>> req = ser.deserialize(ConfigContext.INSTANCE, serialized).getOrThrow();

        Assertions.assertEquals(new Identifier("test", "must_equal"), reg.getId(req.getType()));
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello1"));
        Assertions.assertTrue(req.isInverted());

        Assertions.assertEquals(serialized, ser.serialize(ConfigContext.INSTANCE, req).getOrThrow());

    }

}
