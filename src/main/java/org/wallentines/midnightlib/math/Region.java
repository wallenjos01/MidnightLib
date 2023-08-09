package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

/**
 * Represents a region in 3D space
 */
@SuppressWarnings("unused")
public class Region {

    private final Vec3d lower;
    private final Vec3d upper;

    /**
     * Constructs a region bounded by two points
     * @param point1 The first point
     * @param point2 The second point
     */
    public Region(Vec3d point1, Vec3d point2) {
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

    /**
     * Determines whether a given point is within the region
     * @param vector The point to check
     * @return Whether the point is within the region
     */
    public boolean isWithin(Vec3d vector) {

        return vector.getX() >= lower.getX() && vector.getX() < upper.getX() &&
               vector.getY() >= lower.getY() && vector.getY() < upper.getY() &&
               vector.getZ() >= lower.getZ() && vector.getZ() < upper.getZ();

    }

    /**
     * Determines whether a given point is within the region
     * @param vector The point to check
     * @return Whether the point is within the region
     */
    public boolean isWithin(Vec3i vector) {

        return isWithin(new Vec3d(vector.getX(), vector.getY(), vector.getZ()));
    }

    @Override
    public String toString() {
        return lower.toString() + ";" + upper.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return Objects.equals(lower, region.lower) && Objects.equals(upper, region.upper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }

    /**
     * Parses a region from the given string in the format "x0,y0,z0;x1,y1,z1"
     * @param string The string to parse
     * @return A new region
     */
    public static Region parse(String string) {

        String[] ss = string.split(";");

        Vec3d lower = Vec3d.parse(ss[0]);
        Vec3d extent = Vec3d.parse(ss[1]);

        return new Region(lower, extent);
    }

    public static final Serializer<Region> SERIALIZER = InlineSerializer.of(Objects::toString, Region::parse).or(
            ObjectSerializer.create(
                    Vec3d.SERIALIZER.entry("position", Region::getLowerBound),
                    Vec3d.SERIALIZER.entry("extent", Region::getExtent),
                    Region::new
            ));

}
