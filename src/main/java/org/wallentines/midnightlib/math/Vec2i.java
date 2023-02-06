package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

public class Vec2i {
    private final int x;
    private final int y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double distance(Vec2i other) {
        return Math.sqrt(distanceSquared(other));
    }

    public int distanceSquared(Vec2i other) {
        int distX = this.getX() - other.getX();
        int distY = this.getY() - other.getY();
        return distX * distX + distY * distY;
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
        return new Vec2i(x + i, y + i);
    }

    public Vec2i subtract(int i) {
        return new Vec2i(x - i, y - i);
    }

    public Vec2i multiply(int i) {
        return new Vec2i(x * i, y * i);
    }

    public Vec2i add(Vec2i other) {
        return new Vec2i(x + other.x, y + other.y);
    }

    public Vec2i subtract(Vec2i other) {
        return new Vec2i(x - other.x, y - other.y);
    }

    public Vec2i multiply(Vec2i other) {
        return new Vec2i(x * other.x, y * other.y);
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Vec2i) {
            Vec2i other = (Vec2i) obj;
            return other.getX() == getX() && other.getY() == getY();
        }
        if(obj instanceof Vec2d) {
            Vec2d other = (Vec2d) obj;
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

