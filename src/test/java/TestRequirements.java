import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigPrimitive;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.midnightlib.requirement.MultiRequirement;
import org.wallentines.midnightlib.requirement.Requirement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestRequirements {

    private static class MustEqual extends Requirement<String> {

        private final String data;

        public MustEqual(String value) {
            super(null, false);
            this.data = value;
        }

        @Override
        protected boolean doCheck(String data) {
            return Objects.equals(data, this.data);
        }

        @Override
        public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {
            return null;
        }
    }

    @Test
    public void testMultiRequirement() {

        List<Requirement<String>> lst = getRequirements();

        MultiRequirement<String> req = new MultiRequirement<>(MultiRequirement.Operation.ANY, null, lst);

        Assertions.assertEquals(6, req.getRequirements().size());

        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.ALL, null, lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.exactly(1), null, lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.atLeast(1), null, lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.atMost(2),  null, lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertTrue(req.check("World"));

        req = new MultiRequirement<>(MultiRequirement.Operation.between(2,3), null, lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));
    }

    private static List<Requirement<String>> getRequirements() {

        return Arrays.asList(
                new MustEqual("Hello"),
                new MustEqual("Hello2"),
                new MustEqual("Hello"),
                new MustEqual("Hello3"),
                new MustEqual("Hello3"),
                new MustEqual("Hello3"));
    }

}
