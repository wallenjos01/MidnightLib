package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

public class Vec2d {
    private final double x;
    private final double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }


    public double distance(Vec2d vec2) {
        return Math.sqrt(distanceSquared(vec2));
    }

    public double distanceSquared(Vec2d other) {
        double distX = this.getX() - other.getX();
        double distY = this.getY() - other.getY();
        return distX * distX + distY * distY;
    }

    private static int truncate(double d) {
        return d < 0 ? ((int) d - 1) : (int) d;
    }

    public Vec2i truncate() {

        return new Vec2i(truncate(x), truncate(y));
    }

    @Override
    public String toString() {
        return getX() + "," + getY();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2d)) {
            return false;
        }
        Vec2d other = (Vec2d)obj;
        return other.getX() == this.getX() && other.getY() == this.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Vec2d add(double i) {
        return new Vec2d(x + i, y + i);
    }

    public Vec2d subtract(double i) {
        return new Vec2d(x - i, y - i);
    }

    public Vec2d multiply(double i) {
        return new Vec2d(x * i, y * i);
    }

    public Vec2d add(Vec2d other) {
        return new Vec2d(x + other.x, y + other.y);
    }

    public Vec2d subtract(Vec2d other) {
        return new Vec2d(x - other.x, y - other.y);
    }

    public Vec2d multiply(Vec2d other) {
        return new Vec2d(x * other.x, y * other.y);
    }


    public static Vec2d parse(String str) {

        if(str == null || !str.contains(",")) return null;
        String[] xyz = str.split(",");

        try {
            double x = Double.parseDouble(xyz[0]);
            double y = Double.parseDouble(xyz[1]);

            return new Vec2d(x,y);
        } catch (NumberFormatException ex) {
            return null;
        }

    }

    public static final Serializer<Vec2d> SERIALIZER = org.wallentines.mdcfg.serializer.InlineSerializer.of(Object::toString, Vec2d::parse).or(
        ObjectSerializer.create(
            Serializer.DOUBLE.entry("x", Vec2d::getX),
            Serializer.DOUBLE.entry("y", Vec2d::getY),
            Vec2d::new
        ));

}

