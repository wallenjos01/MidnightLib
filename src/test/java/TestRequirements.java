import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigObject;
import org.wallentines.mdcfg.ConfigPrimitive;
import org.wallentines.midnightlib.requirement.MultiRequirement;
import org.wallentines.midnightlib.requirement.Requirement;
import org.wallentines.midnightlib.requirement.RequirementType;

import java.util.Arrays;
import java.util.List;

public class TestRequirements {

    private static class MustEqual implements RequirementType<String> {

        @Override
        public boolean check(String data, ConfigObject config, Requirement<String> req) {

            Assertions.assertNotNull(req);
            Assertions.assertEquals(this, req.getType());

            return data.equals(config.asString());
        }
    }

    @Test
    public void testMultiRequirement() {

        List<Requirement<String>> lst = getRequirements();

        MultiRequirement<String> req = new MultiRequirement<>(MultiRequirement.Operation.ANY, lst);

        Assertions.assertEquals(6, req.getRequirements().size());

        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.ALL, lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.exactly(1), lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.atLeast(1), lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.atMost(2), lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertTrue(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.between(2,3), lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));
    }

    private static List<Requirement<String>> getRequirements() {
        MustEqual mst = new MustEqual();

        return Arrays.asList(
                new Requirement<>(mst, new ConfigPrimitive("Hello")),
                new Requirement<>(mst, new ConfigPrimitive("Hello2")),
                new Requirement<>(mst, new ConfigPrimitive("Hello")),
                new Requirement<>(mst, new ConfigPrimitive("Hello3")),
                new Requirement<>(mst, new ConfigPrimitive("Hello3")),
                new Requirement<>(mst, new ConfigPrimitive("Hello3")));
    }

}
