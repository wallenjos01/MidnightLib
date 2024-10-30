import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.mdcfg.serializer.ConfigContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.midnightlib.math.*;

public class TestMath {

    @Test
    public void testColor() {

        Color gray = new Color(100, 100, 100);

        Assertions.assertEquals(100, gray.getRed());
        Assertions.assertEquals(100, gray.getGreen());
        Assertions.assertEquals(100, gray.getBlue());
        Assertions.assertEquals("#646464", gray.toHex());
        Assertions.assertEquals("646464", gray.toPlainHex());
        Assertions.assertEquals(6579300, gray.toDecimal());
        Assertions.assertEquals(8, gray.toRGBI());

        Color lightGray = gray.multiply(1.5);

        Assertions.assertEquals(150, lightGray.getRed());
        Assertions.assertEquals(150, lightGray.getGreen());
        Assertions.assertEquals(150, lightGray.getBlue());
        Assertions.assertEquals("#969696", lightGray.toHex());
        Assertions.assertEquals("969696", lightGray.toPlainHex());
        Assertions.assertEquals(9868950, lightGray.toDecimal());
        Assertions.assertEquals(7, lightGray.toRGBI());

        Assertions.assertEquals(50 * 50 * 3, gray.getDistanceSquaredTo(lightGray));
        Assertions.assertEquals(50 * 50 * 3, lightGray.getDistanceSquaredTo(gray));

        Assertions.assertEquals(50 * Math.sqrt(3), gray.getDistanceTo(lightGray));
        Assertions.assertEquals(50 * Math.sqrt(3), lightGray.getDistanceTo(gray));

        String unparsed = "123456";
        Color parsed = Color.parseOrNull(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(18, parsed.getRed());
        Assertions.assertEquals(52, parsed.getGreen());
        Assertions.assertEquals(86, parsed.getBlue());

        String unparsedPound = "#563412";
        Color parsedPound = Color.parseOrNull(unparsedPound);

        Assertions.assertNotNull(parsedPound);
        Assertions.assertEquals(86, parsedPound.getRed());
        Assertions.assertEquals(52, parsedPound.getGreen());
        Assertions.assertEquals(18, parsedPound.getBlue());

        Assertions.assertEquals(Color.WHITE, Color.fromRGBI(15));

        ConfigSection unparsedSection = new ConfigSection().with("red", 255).with("green", 12).with("blue", 200);
        SerializeResult<Color> result = Color.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());
        Assertions.assertEquals(255, result.getOrThrow().getRed());
        Assertions.assertEquals(12, result.getOrThrow().getGreen());
        Assertions.assertEquals(200, result.getOrThrow().getBlue());

    }

    @Test
    public void testRGBA() {

        Color.RGBA gray = new Color.RGBA (100, 100, 100, 100);

        Assertions.assertEquals(100, gray.getRed());
        Assertions.assertEquals(100, gray.getGreen());
        Assertions.assertEquals(100, gray.getBlue());
        Assertions.assertEquals(100, gray.getAlpha());
        Assertions.assertEquals("#64646464", gray.toHex());
        Assertions.assertEquals("64646464", gray.toPlainHex());
        Assertions.assertEquals(1684300900, gray.toDecimal());
        Assertions.assertEquals(8, gray.toRGBI());

        Color lightGray = gray.multiply(1.5);

        Assertions.assertEquals(150, lightGray.getRed());
        Assertions.assertEquals(150, lightGray.getGreen());
        Assertions.assertEquals(150, lightGray.getBlue());
        Assertions.assertEquals(100, lightGray.asRGBA().getAlpha());
        Assertions.assertEquals("#96969664", lightGray.toHex());
        Assertions.assertEquals("96969664", lightGray.toPlainHex());
        Assertions.assertEquals(1687590550, lightGray.toDecimal());
        Assertions.assertEquals(7, lightGray.toRGBI());

        Assertions.assertEquals(50 * 50 * 3, gray.getDistanceSquaredTo(lightGray));
        Assertions.assertEquals(50 * 50 * 3, lightGray.getDistanceSquaredTo(gray));

        Assertions.assertEquals(50 * Math.sqrt(3), gray.getDistanceTo(lightGray));
        Assertions.assertEquals(50 * Math.sqrt(3), lightGray.getDistanceTo(gray));

        String unparsed = "37123456";
        Color parsed = Color.parseOrNull(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertInstanceOf(Color.RGBA.class, parsed);
        Assertions.assertEquals(18, parsed.getRed());
        Assertions.assertEquals(52, parsed.getGreen());
        Assertions.assertEquals(86, parsed.getBlue());
        Assertions.assertEquals(55, ((Color.RGBA) parsed).getAlpha());

        String unparsedPound = "#37563412";
        Color parsedPound = Color.parseOrNull(unparsedPound);

        Assertions.assertNotNull(parsedPound);
        Assertions.assertInstanceOf(Color.RGBA.class, parsedPound);
        Assertions.assertEquals(86, parsedPound.getRed());
        Assertions.assertEquals(52, parsedPound.getGreen());
        Assertions.assertEquals(18, parsedPound.getBlue());
        Assertions.assertEquals(55, ((Color.RGBA) parsedPound).getAlpha());

        ConfigSection unparsedSection = new ConfigSection().with("red", 255).with("green", 12).with("blue", 200).with("alpha", 22);
        SerializeResult<Color> result = Color.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());

        Color c = result.getOrThrow();
        Assertions.assertInstanceOf(Color.RGBA.class, c);

        Assertions.assertEquals(255, c.getRed());
        Assertions.assertEquals(12, c.getGreen());
        Assertions.assertEquals(200, c.getBlue());
        Assertions.assertEquals(22, c.asRGBA().getAlpha());

    }


    @Test
    public void testVec2i() {

        Vec2i vec2i = new Vec2i(1, 3);
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());

        Vec2i added = vec2i.add(3);
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());
        Assertions.assertEquals(4, added.getX());
        Assertions.assertEquals(6, added.getY());

        Assertions.assertEquals(18, vec2i.distanceSquared(added));
        Assertions.assertEquals(Math.sqrt(2) * 3, added.distance(vec2i), 0.01);

        added = vec2i.add(new Vec2i(10, 1));
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());
        Assertions.assertEquals(11, added.getX());
        Assertions.assertEquals(4, added.getY());

        Vec2i subtracted = vec2i.subtract(3);
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());
        Assertions.assertEquals(-2, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());

        subtracted = vec2i.subtract(new Vec2i(1, 3));
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());
        Assertions.assertEquals(0, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());

        Vec2i multiplied = vec2i.multiply(3);
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());
        Assertions.assertEquals(3, multiplied.getX());
        Assertions.assertEquals(9, multiplied.getY());

        multiplied = vec2i.multiply(new Vec2i(3, 1));
        Assertions.assertEquals(1, vec2i.getX());
        Assertions.assertEquals(3, vec2i.getY());
        Assertions.assertEquals(3, multiplied.getX());
        Assertions.assertEquals(3, multiplied.getY());

        Assertions.assertEquals(new Vec2i(3,3), multiplied);

        String unparsed = "12,-11";
        Vec2i parsed = Vec2i.parse(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(12, parsed.getX());
        Assertions.assertEquals(-11, parsed.getY());


        ConfigSection unparsedSection = new ConfigSection().with("x", 3).with("y", 1);
        SerializeResult<Vec2i> result = Vec2i.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());
        Assertions.assertEquals(new Vec2i(3,1), result.getOrThrow());

    }

    @Test
    public void testVec2d() {

        Vec2d vec2d = new Vec2d(1.5, 3.0);
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3.0, vec2d.getY());

        Vec2i truncated = vec2d.truncate();
        Assertions.assertEquals(new Vec2i(1, 3), truncated);

        Vec2d added = vec2d.add(3.0);
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3.0, vec2d.getY());
        Assertions.assertEquals(4.5, added.getX());
        Assertions.assertEquals(6.0, added.getY());

        Assertions.assertEquals(18, vec2d.distanceSquared(added));
        Assertions.assertEquals(Math.sqrt(2) * 3, added.distance(vec2d), 0.01);

        added = vec2d.add(new Vec2d(10, 1));
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3, vec2d.getY());
        Assertions.assertEquals(11.5, added.getX());
        Assertions.assertEquals(4, added.getY());

        Vec2d subtracted = vec2d.subtract(3);
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3, vec2d.getY());
        Assertions.assertEquals(-1.5, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());

        subtracted = vec2d.subtract(new Vec2d(1.5, 3));
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3, vec2d.getY());
        Assertions.assertEquals(0, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());

        Vec2d multiplied = vec2d.multiply(3);
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3.0, vec2d.getY());
        Assertions.assertEquals(4.5, multiplied.getX());
        Assertions.assertEquals(9.0, multiplied.getY());

        multiplied = vec2d.multiply(new Vec2d(2, 1));
        Assertions.assertEquals(1.5, vec2d.getX());
        Assertions.assertEquals(3, vec2d.getY());
        Assertions.assertEquals(3, multiplied.getX());
        Assertions.assertEquals(3, multiplied.getY());

        Assertions.assertEquals(new Vec2d(3,3), multiplied);

        String unparsed = "12.1,-11";
        Vec2d parsed = Vec2d.parse(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(12.1, parsed.getX());
        Assertions.assertEquals(-11.0, parsed.getY());


        ConfigSection unparsedSection = new ConfigSection().with("x", 3).with("y", 1);
        SerializeResult<Vec2d> result = Vec2d.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());
        Assertions.assertEquals(new Vec2d(3,1), result.getOrThrow());
    }

    @Test
    public void testVec3i() {

        Vec3i vec3i = new Vec3i(1, 3, 2);
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(2, vec3i.getZ());

        Vec3i added = vec3i.add(3);
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(2, vec3i.getZ());
        Assertions.assertEquals(4, added.getX());
        Assertions.assertEquals(6, added.getY());
        Assertions.assertEquals(5, added.getZ());

        Assertions.assertEquals(27, vec3i.distanceSquared(added));
        Assertions.assertEquals(3 * Math.sqrt(3), added.distance(vec3i), 0.01);

        added = vec3i.add(new Vec3i(10, 1, 2));
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(2, vec3i.getZ());
        Assertions.assertEquals(11, added.getX());
        Assertions.assertEquals(4, added.getY());
        Assertions.assertEquals(4, added.getZ());

        Vec3i subtracted = vec3i.subtract(3);
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(2, vec3i.getZ());
        Assertions.assertEquals(-2, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());
        Assertions.assertEquals(-1, subtracted.getZ());

        subtracted = vec3i.subtract(new Vec3i(1, 3, 2));
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(2, vec3i.getZ());
        Assertions.assertEquals(0, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());
        Assertions.assertEquals(0, subtracted.getZ());

        Vec3i multiplied = vec3i.multiply(3);
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(3, multiplied.getX());
        Assertions.assertEquals(9, multiplied.getY());
        Assertions.assertEquals(6, multiplied.getZ());

        multiplied = vec3i.multiply(new Vec3i(3, 1, 5));
        Assertions.assertEquals(1, vec3i.getX());
        Assertions.assertEquals(3, vec3i.getY());
        Assertions.assertEquals(2, vec3i.getZ());
        Assertions.assertEquals(3, multiplied.getX());
        Assertions.assertEquals(3, multiplied.getY());
        Assertions.assertEquals(10, multiplied.getZ());

        Assertions.assertEquals(new Vec3i(3,3, 10), multiplied);

        String unparsed = "12,-11,3";
        Vec3i parsed = Vec3i.parse(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(12, parsed.getX());
        Assertions.assertEquals(-11, parsed.getY());
        Assertions.assertEquals(3, parsed.getZ());

        ConfigSection unparsedSection = new ConfigSection().with("x", 3).with("y", 1).with("z", 2);
        SerializeResult<Vec3i> result = Vec3i.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());
        Assertions.assertEquals(new Vec3i(3,1,2), result.getOrThrow());

    }

    @Test
    public void testVec3d() {

        Vec3d vec3d = new Vec3d(1.5, 3.0, 2.0);
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3.0, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());

        Vec3i truncated = vec3d.truncate();
        Assertions.assertEquals(new Vec3i(1, 3, 2), truncated);

        Vec3d added = vec3d.add(3.0);
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3.0, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());
        Assertions.assertEquals(4.5, added.getX());
        Assertions.assertEquals(6.0, added.getY());
        Assertions.assertEquals(5.0, added.getZ());

        Assertions.assertEquals(27, vec3d.distanceSquared(added));
        Assertions.assertEquals(Math.sqrt(3) * 3, added.distance(vec3d), 0.01);

        added = vec3d.add(new Vec3d(10, 1, 2.5));
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());
        Assertions.assertEquals(11.5, added.getX());
        Assertions.assertEquals(4, added.getY());
        Assertions.assertEquals(4.5, added.getZ());

        Vec3d subtracted = vec3d.subtract(3);
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());
        Assertions.assertEquals(-1.5, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());
        Assertions.assertEquals(-1.0, subtracted.getZ());

        subtracted = vec3d.subtract(new Vec3d(1.5, 3, 10.0));
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());
        Assertions.assertEquals(0, subtracted.getX());
        Assertions.assertEquals(0, subtracted.getY());
        Assertions.assertEquals(-8.0, subtracted.getZ());

        Vec3d multiplied = vec3d.multiply(3);
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3.0, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());
        Assertions.assertEquals(4.5, multiplied.getX());
        Assertions.assertEquals(9.0, multiplied.getY());
        Assertions.assertEquals(6.0, multiplied.getZ());

        multiplied = vec3d.multiply(new Vec3d(2, 1, 0));
        Assertions.assertEquals(1.5, vec3d.getX());
        Assertions.assertEquals(3, vec3d.getY());
        Assertions.assertEquals(2.0, vec3d.getZ());
        Assertions.assertEquals(3, multiplied.getX());
        Assertions.assertEquals(3, multiplied.getY());
        Assertions.assertEquals(0, multiplied.getZ());

        Assertions.assertEquals(new Vec3d(3,3, 0), multiplied);

        String unparsed = "12.1,-11,0.5";
        Vec3d parsed = Vec3d.parse(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(12.1, parsed.getX());
        Assertions.assertEquals(-11.0, parsed.getY());
        Assertions.assertEquals(0.5, parsed.getZ());

        ConfigSection unparsedSection = new ConfigSection().with("x", 3).with("y", 1).with("z", 2);
        SerializeResult<Vec3d> result = Vec3d.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());
        Assertions.assertEquals(new Vec3d(3,1,2), result.getOrThrow());

    }

    @Test
    public void testCuboidRegion() {

        CuboidRegion region = new CuboidRegion(new Vec3d(1,6,3), new Vec3d(6.5,4,8));

        Assertions.assertEquals(new Vec3d(1,4,3), region.getLowerBound());
        Assertions.assertEquals(new Vec3d(6.5,6,8), region.getUpperBound());
        Assertions.assertEquals(new Vec3d(5.5, 2, 5), region.getExtent());

        Assertions.assertTrue(region.isWithin(new Vec3d(2.1,5.3,4)));
        Assertions.assertFalse(region.isWithin(new Vec3d(0,9,4)));
        Assertions.assertTrue(region.isWithin(new Vec3i(2,5,4)));
        Assertions.assertFalse(region.isWithin(new Vec3i(0,9,4)));

        String unparsed = "0,10,0;10,0,-10";
        CuboidRegion parsed = CuboidRegion.parse(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(new Vec3d(0,0,-10), parsed.getLowerBound());
        Assertions.assertEquals(new Vec3d(10,10,0), parsed.getUpperBound());
        Assertions.assertEquals(new Vec3d(10,10,10), parsed.getExtent());

        ConfigSection unparsedSection = new ConfigSection().with("lower", "3,6,9").with("upper", "12,33,15");
        SerializeResult<CuboidRegion> result = CuboidRegion.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete());
        Assertions.assertEquals(new Vec3d(3,6,9), result.getOrThrow().getLowerBound());
        Assertions.assertEquals(new Vec3d(12,33,15), result.getOrThrow().getUpperBound());

    }

    @Test
    public void testSphereRegion() {

        SphereRegion region = new SphereRegion(new Vec3d(1,-2,3), 3.0);

        Assertions.assertEquals(new Vec3d(1,-2,3), region.getOrigin());
        Assertions.assertEquals(3.0, region.getRadius());

        Assertions.assertTrue(region.isWithin(new Vec3d(2.1,0,4)));
        Assertions.assertFalse(region.isWithin(new Vec3d(0,9.3,4.4)));
        Assertions.assertTrue(region.isWithin(new Vec3i(2,-1,4)));
        Assertions.assertFalse(region.isWithin(new Vec3i(0,9,4)));

        String unparsed = "0,10,0r3.5";
        SphereRegion parsed = SphereRegion.parse(unparsed);

        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(new Vec3d(0,10,0), parsed.getOrigin());
        Assertions.assertEquals(3.5, parsed.getRadius());

        ConfigSection unparsedSection = new ConfigSection().with("origin", "3,6,9").with("radius", 12.5);
        SerializeResult<SphereRegion> result = SphereRegion.SERIALIZER.deserialize(ConfigContext.INSTANCE, unparsedSection);

        Assertions.assertTrue(result.isComplete(), result.getError());
        Assertions.assertEquals(new Vec3d(3,6,9), result.getOrThrow().getOrigin());
        Assertions.assertEquals(12.5, result.getOrThrow().getRadius());

    }


}
