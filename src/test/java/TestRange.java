import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigPrimitive;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.midnightlib.math.Range;

import java.util.Arrays;

public class TestRange {

    @Test
    public void testExact() {

        Range<Integer> range = Range.exactly(1);

        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));


        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive(1)).getOrThrow();

        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("1")).getOrThrow();

        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));
    }

    @Test
    public void testGreater() {

        Range<Integer> range = Range.greaterThan(1);

        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertFalse(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive(">1")).getOrThrow();
        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertFalse(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

    }

    @Test
    public void testAtLeast() {

        Range<Integer> range = Range.atLeast(1);

        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive(">=1")).getOrThrow();

        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

    }

    @Test
    public void testLess() {

        Range<Integer> range = Range.lessThan(1);

        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertFalse(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("<1")).getOrThrow();

        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertFalse(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));
    }

    @Test
    public void testAtMost() {

        Range<Integer> range = Range.atMost(1);

        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("<=1")).getOrThrow();

        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

    }

    @Test
    public void testInterval() {
        Range<Integer> range = Range.openInterval(0,2);
        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.closedInterval(0,2);
        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

        range = Range.closedOpenInterval(0,2);
        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.openClosedInterval(0,2);
        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));


        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("(0,2)")).getOrThrow();
        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("[0,2]")).getOrThrow();
        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("[0,2)")).getOrThrow();
        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("(0,2]")).getOrThrow();
        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));
    }

    @Test
    public void testSet() {
        Range<Integer> range = Range.inSet(Arrays.asList(0,2));
        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertFalse(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));

        range = Range.INTEGER.deserialize(ConfigContext.INSTANCE, new ConfigPrimitive("{0,2}")).getOrThrow();
        Assertions.assertTrue(range.isWithin(0));
        Assertions.assertFalse(range.isWithin(1));
        Assertions.assertTrue(range.isWithin(2));
    }

    @Test
    public void testSupplied() {

        Range<Integer> range = Range.supplied(() -> 1);
        Assertions.assertFalse(range.isWithin(0));
        Assertions.assertTrue(range.isWithin(1));
        Assertions.assertFalse(range.isWithin(2));
    }

}
