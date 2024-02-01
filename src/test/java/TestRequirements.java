import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.requirement.MultiRequirement;
import org.wallentines.midnightlib.requirement.Requirement;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TestRequirements {


    private static class MustEqual implements Predicate<String> {

        private final String data;

        public MustEqual(String value) {
            this.data = value;
        }

        @Override
        public boolean test(String data) {
            return Objects.equals(data, this.data);
        }
    }

    @Test
    public void testMultiRequirement() {

        List<Requirement<String, Predicate<String>>> lst = getRequirements();

        MultiRequirement<String, Predicate<String>> req = new MultiRequirement<>(Range.atLeast(1), null, lst);

        Assertions.assertEquals(6, req.getRequirements().size());

        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(Range.all(), null, lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(Range.exactly(1), null, lst);
        Assertions.assertFalse(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(Range.atLeast(1), null, lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));

        req = new MultiRequirement<>(Range.atMost(2),  null, lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertTrue(req.check("Hello2"));
        Assertions.assertFalse(req.check("Hello3"));
        Assertions.assertTrue(req.check("World"));

        req = new MultiRequirement<>(Range.closedInterval(2,3), null, lst);
        Assertions.assertTrue(req.check("Hello"));
        Assertions.assertFalse(req.check("Hello2"));
        Assertions.assertTrue(req.check("Hello3"));
        Assertions.assertFalse(req.check("World"));
    }

    private static List<Requirement<String, Predicate<String>>> getRequirements() {

       return Arrays.asList(
               new Requirement<>(new MustEqual("Hello")),
               new Requirement<>(new MustEqual("Hello2")),
               new Requirement<>(new MustEqual("Hello")),
               new Requirement<>(new MustEqual("Hello3")),
               new Requirement<>(new MustEqual("Hello3")),
               new Requirement<>(new MustEqual("Hello3"))
       );
    }

}
