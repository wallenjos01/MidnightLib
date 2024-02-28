import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.requirement.Check;
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
            return SerializeResult.failure("");
        }
    }

    @Test
    public void testMultiRequirement() {

        List<Requirement<String>> lst = getRequirements();

        Requirement<String> req = Requirement.composite(Range.atLeast(1), lst);

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

    private static List<Requirement<String>> getRequirements() {

       return Arrays.asList(
               Requirement.simple(new MustEqual("Hello")),
               Requirement.simple(new MustEqual("Hello2")),
               Requirement.simple(new MustEqual("Hello")),
               Requirement.simple(new MustEqual("Hello3")),
               Requirement.simple(new MustEqual("Hello3")),
               Requirement.simple(new MustEqual("Hello3"))
       );
    }

}
