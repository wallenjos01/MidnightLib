package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

public class Vec3d {
    private final double x;
    private final double y;
    private final double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double distance(Vec3d other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double distanceSquared(Vec3d other) {
        double ax = this.getX() - other.getX();
        double ay = this.getY() - other.getY();
        double az = this.getZ() - other.getZ();
        return ax * ax + ay * ay + az * az;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3d)) {
            return false;
        }
        Vec3d other = (Vec3d)obj;
        return other.getX() == this.getX() && other.getY() == this.getY() && other.getZ() == this.getZ();
    }

    private static int truncate(double d) {
        return d < 0 ? ((int) d - 1) : (int) d;
    }

    public Vec3i truncate() {

        return new Vec3i(truncate(x), truncate(y), truncate(z));
    }

    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ();
    }

    public static Vec3d parse(String str) {

        if(str == null || !str.contains(",")) return null;
        String[] xyz = str.split(",");

        try {
            double x = Double.parseDouble(xyz[0]);
            double y = Double.parseDouble(xyz[1]);
            double z = Double.parseDouble(xyz[2]);

            return new Vec3d(x,y,z);
        } catch (NumberFormatException ex) {
            return null;
        }

    }

    public Vec3d add(double i) {
        return new Vec3d(x + i, y + i, z + i);
    }

    public Vec3d subtract(int i) {
        return new Vec3d(x - i, y - i, z - i);
    }
    public Vec3d multiply(double i) {
        return new Vec3d(x * i, y * i, z * i);
    }

    public Vec3d add(Vec3d i) {
        return new Vec3d(x + i.x, y + i.y, z + i.z);
    }

    public Vec3d subtract(Vec3d other) {
        return new Vec3d(x - other.x, y - other.y, z - other.z);
    }
    public Vec3d multiply(Vec3d i) {
        return new Vec3d(x * i.x, y * i.y, z * i.z);
    }


    public static final Serializer<Vec3d> SERIALIZER = InlineSerializer.of(Object::toString, Vec3d::parse).or(
        ObjectSerializer.create(
            Serializer.DOUBLE.entry("x", Vec3d::getX),
            Serializer.DOUBLE.entry("y", Vec3d::getY),
            Serializer.DOUBLE.entry("z", Vec3d::getZ),
            Vec3d::new
        ));


}