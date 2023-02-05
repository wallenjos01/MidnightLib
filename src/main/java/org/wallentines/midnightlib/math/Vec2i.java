package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

public class Vec2i {
    private final int[] data;

    public Vec2i(int x, int y) {
        this.data = new int[]{x, y};
    }

    public int getX() {
        return this.data[0];
    }

    public int getY() {
        return this.data[1];
    }

    public double distance(Vec2i vec2) {
        return Math.sqrt(Math.pow((this.getX() - vec2.getX()), 2) + Math.pow((this.getY() - vec2.getY()), 2));
    }

    public static Vec2i parse(String str) {

        if(str == null || !str.contains(",")) return null;
        String[] xyz = str.split(",");

        try {
            int x = Integer.parseInt(xyz[0]);
            int y = Integer.parseInt(xyz[1]);

            return new Vec2i(x,y);
        } catch (NumberFormatException ex) {
            return null;
        }

    }

    @Override
    public String toString() {
        return getX() + "," + getY();
    }

    public Vec2i add(int i) {
        return new Vec2i(data[0] + i, data[1] + i);
    }

    public Vec2i multiply(int i) {
        return new Vec2i(data[0] * i, data[1] * i);
    }

    public Vec2i add(Vec2i i) {
        return new Vec2i(data[0] + i.data[0], data[1] + i.data[1]);
    }

    public Vec2i multiply(Vec2i i) {
        return new Vec2i(data[0] * i.data[0], data[1] * i.data[1]);
    }

    public Vec2i subtract(int i) {
        return new Vec2i(data[0] - i, data[1] - i);
    }

    public Vec2i subtract(Vec2i i) {
        return new Vec2i(data[0] - i.data[0], data[1] - i.data[1]);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Vec3i) {
            Vec3i other = (Vec3i) obj;
            return other.getX() == getX() && other.getY() == getY();
        }
        if(obj instanceof Vec3d) {
            Vec3d other = (Vec3d) obj;
            return other.getX() == getX() && other.getY() == getY();
        }

        return false;
    }

    public static final Serializer<Vec2i> SERIALIZER = InlineSerializer.of(Object::toString, Vec2i::parse).or(
            ObjectSerializer.create(
                    Serializer.INT.entry("x", Vec2i::getX),
                    Serializer.INT.entry("y", Vec2i::getY),
                    Vec2i::new
            ));
}

