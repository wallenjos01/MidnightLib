package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

public class Vec3i {
    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public double distance(Vec3i other) {
        return Math.sqrt(distanceSquared(other));
    }

    public int distanceSquared(Vec3i other) {
        int distX = this.getX() - other.getX();
        int distY = this.getY() - other.getY();
        int distZ = this.getZ() - other.getZ();
        return distX * distX + distY * distY + distZ * distZ;
    }

    public static Vec3i parse(String str) {

        if(str == null || !str.contains(",")) return null;
        String[] xyz = str.split(",");

        try {
            int x = Integer.parseInt(xyz[0]);
            int y = Integer.parseInt(xyz[1]);
            int z = Integer.parseInt(xyz[2]);

            return new Vec3i(x,y,z);
        } catch (NumberFormatException ex) {
            return null;
        }

    }

    public Vec3i add(int i) {
        return new Vec3i(x + i, y + i, z + i);
    }

    public Vec3i subtract(int i) {
        return new Vec3i(x - i, y - i, z - i);
    }

    public Vec3i multiply(int i) {
        return new Vec3i(x * i, y * i, z * i);
    }

    public Vec3i add(Vec3i other) {
        return new Vec3i(x + other.x, y + other.y, z + other.z);
    }

    public Vec3i subtract(Vec3i other) {
        return new Vec3i(x - other.x, y - other.y, z - other.z);
    }

    public Vec3i multiply(Vec3i other) {
        return new Vec3i(x * other.x, y * other.y, z * other.z);
    }

    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Vec3i) {
            Vec3i other = (Vec3i) obj;
            return other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ();
        }
        if(obj instanceof Vec3d) {
            Vec3d other = (Vec3d) obj;
            return other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ();
        }

        return false;
    }

    public static final Serializer<Vec3i> SERIALIZER = InlineSerializer.of(Object::toString, Vec3i::parse).or(
        ObjectSerializer.create(
            Serializer.INT.entry("x", Vec3i::getX),
            Serializer.INT.entry("y", Vec3i::getY),
            Serializer.INT.entry("z", Vec3i::getZ),
            Vec3i::new
        ));
}

