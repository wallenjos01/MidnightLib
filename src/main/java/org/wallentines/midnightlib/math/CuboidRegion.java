package org.wallentines.midnightlib.math;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.*;

import java.util.Objects;

/**
 * Represents a cuboid region in 3D space
 */
public class CuboidRegion implements Region {

    private final Vec3d lower;
    private final Vec3d upper;

    /**
     * Constructs a region bounded by two points
     * @param point1 The first point
     * @param point2 The second point
     */
    public CuboidRegion(Vec3d point1, Vec3d point2) {
        this.lower = new Vec3d(
                Math.min(point1.getX(), point2.getX()),
                Math.min(point1.getY(), point2.getY()),
                Math.min(point1.getZ(), point2.getZ())
            );
        this.upper = new Vec3d(
                Math.max(point1.getX(), point2.getX()),
                Math.max(point1.getY(), point2.getY()),
                Math.max(point1.getZ(), point2.getZ())
        );
    }

    /**
     * Gets the lower bound of the region
     * @return The lower bound
     */
    public Vec3d getLowerBound() {
        return lower;
    }

    /**
     * Gets the upper bound of the region
     * @return The upper bound
     */
    public Vec3d getUpperBound() {
        return upper;
    }

    /**
     * Gets the extent (size) of the region
     * @return The extent of the region
     */
    public Vec3d getExtent() {
        return upper.subtract(lower);
    }

    @Override
    public boolean isWithin(Vec3d vector) {

        return vector.getX() >= lower.getX() && vector.getX() < upper.getX() &&
               vector.getY() >= lower.getY() && vector.getY() < upper.getY() &&
               vector.getZ() >= lower.getZ() && vector.getZ() < upper.getZ();

    }

    @Override
    public CuboidRegion getBoundingBox() {
        return this;
    }

    @Override
    public <T> SerializeResult<T> serialize(SerializeContext<T> context) {
        return SERIALIZER.serialize(context, this);
    }

    @Override
    public String toString() {
        return lower.toString() + ";" + upper.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuboidRegion region = (CuboidRegion) o;
        return Objects.equals(lower, region.lower) && Objects.equals(upper, region.upper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }


    /**
     * Parses a region from the given string in the format "X0,Y0,Z0;X1,Y1,Z1"
     * @param string The string to parse
     * @return A new cuboid region, or null if the string is not in the correct format
     */
    @Nullable
    public static CuboidRegion parse(String string) {

        String[] ss = string.split(";");

        if(ss.length != 2) return null;

        Vec3d lower = Vec3d.parse(ss[0]);
        Vec3d extent = Vec3d.parse(ss[1]);

        if(lower == null || extent == null) {
            return null;
        }

        return new CuboidRegion(lower, extent);
    }

    public static final Serializer<CuboidRegion> SERIALIZER =
            ObjectSerializer.create(
                    Vec3d.SERIALIZER.entry("lower", CuboidRegion::getLowerBound),
                    Vec3d.SERIALIZER.entry("upper", CuboidRegion::getUpperBound),
                    CuboidRegion::new
            ).or(InlineSerializer.of(Objects::toString, CuboidRegion::parse));

}
