package org.wallentines.midnightlib.math;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.*;

import java.util.Objects;

/**
 * A class representing a spherical region in 3D space
 */
public class SphereRegion implements Region {

    private final Vec3d origin;
    private final double radius;

    public SphereRegion(Vec3d origin, double radius) {
        this.origin = origin;
        this.radius = radius;
    }

    public Vec3d getOrigin() {
        return origin;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean isWithin(Vec3d vector) {
        return vector.distanceSquared(origin) <= (radius * radius);
    }

    @Override
    public CuboidRegion getBoundingBox() {
        return new CuboidRegion(origin.subtract(radius), origin.add(radius));
    }

    @Override
    public <T> SerializeResult<T> serialize(SerializeContext<T> context) {
        return SERIALIZER.serialize(context, this);
    }

    @Override
    public String toString() {
        return origin.toString() + "r" + radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SphereRegion region = (SphereRegion) o;
        return Objects.equals(origin, region.origin) && radius == region.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, radius);
    }

    /**
     * Parses a region from the given string in the format "X,Y,ZrR"
     * @param string The string to parse
     * @return A new spherical region, or null if the string is not in the correct format
     */
    @Nullable
    public static SphereRegion parse(String string) {

        String[] ss = string.split("r");

        if(ss.length != 2) return null;

        Vec3d origin = Vec3d.parse(ss[0]);
        if(origin == null) {
            return null;
        }

        double radius;
        try {
            radius = Double.parseDouble(ss[1]);
        } catch (NumberFormatException ex) {
            return null;
        }

        return new SphereRegion(origin, radius);
    }

    public static final Serializer<SphereRegion> SERIALIZER = ObjectSerializer.create(
            Vec3d.SERIALIZER.entry("origin", SphereRegion::getOrigin),
            Serializer.DOUBLE.entry("radius", SphereRegion::getRadius),
            SphereRegion::new
    ).or(InlineSerializer.of(Objects::toString, SphereRegion::parse));

}
