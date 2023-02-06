package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

public class Region {

    private final Vec3d lower;
    private final Vec3d upper;

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

    public Vec3d getLowerBound() {
        return lower;
    }

    public Vec3d getUpperBound() {
        return upper;
    }

    public Vec3d getExtent() {
        return upper.subtract(lower);
    }

    public boolean isWithin(Vec3d vector) {

        return vector.getX() >= lower.getX() && vector.getX() < upper.getX() &&
               vector.getY() >= lower.getY() && vector.getY() < upper.getY() &&
               vector.getZ() >= lower.getZ() && vector.getZ() < upper.getZ();

    }

    public boolean isWithin(Vec3i vector) {

        return isWithin(new Vec3d(vector.getX(), vector.getY(), vector.getZ()));
    }

    public String toString() {
        return lower.toString() + ";" + upper.toString();
    }

    public static Region parse(String s) {

        String[] ss = s.split(";");

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
