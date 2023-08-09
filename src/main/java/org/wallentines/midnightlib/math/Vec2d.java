package org.wallentines.midnightlib.math;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

/**
 * A two-dimensional vector of doubles
 */
public class Vec2d {

    private final double x;
    private final double y;

    /**
     * Constructs a vector from two doubles
     * @param x The first value
     * @param y The second value
     */
    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the first (x) value of the vector
     * @return The first value
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the second (y) value of the vector
     * @return The second value
     */
    public double getY() {
        return this.y;
    }

    /**
     * Determines the distance to another two-dimensional vector of doubles
     * @param other The other vector
     * @return The distance between the vectors
     */
    public double distance(Vec2d other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Determines the squared distance to another two-dimensional vector of doubles
     * @param other The other vector
     * @return The squared distance between the vectors
     */
    public double distanceSquared(Vec2d other) {
        double distX = this.getX() - other.getX();
        double distY = this.getY() - other.getY();
        return distX * distX + distY * distY;
    }

    /**
     * Converts the vector into a two-dimensional vector of integers by truncating its values
     * @return A truncated vector
     */
    public Vec2i truncate() {

        return new Vec2i(truncate(x), truncate(y));
    }

    /**
     * Creates a new vector by adding a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec2d add(double value) {
        return new Vec2d(x + value, y + value);
    }


    /**
     * Creates a new vector by subtracting a value from all values in the vector
     * @param value The value to subtract
     * @return A new vector
     */
    public Vec2d subtract(double value) {
        return new Vec2d(x - value, y - value);
    }


    /**
     * Creates a new vector by multiplying a value by all values in the vector
     * @param value The value to multiply
     * @return A new vector
     */
    public Vec2d multiply(double value) {
        return new Vec2d(x * value, y * value);
    }


    /**
     * Creates a new vector by adding another vector's values to this vector's values
     * @param other The vector to add
     * @return A new vector
     */
    public Vec2d add(Vec2d other) {
        return new Vec2d(x + other.x, y + other.y);
    }


    /**
     * Creates a new vector by subtracting another vector's values to this vector's values
     * @param other The vector to subtract
     * @return A new vector
     */
    public Vec2d subtract(Vec2d other) {
        return new Vec2d(x - other.x, y - other.y);
    }


    /**
     * Creates a new vector by multiplying another vector's values to this vector's values
     * @param other The vector to multiply
     * @return A new vector
     */
    public Vec2d multiply(Vec2d other) {
        return new Vec2d(x * other.x, y * other.y);
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


    /**
     * Parses a two-dimensional vector of doubles from a string in the format "x,y"
     * @param str The string to parse
     * @return A parsed vector, or null if the String is not formatted correctly
     */
    @Nullable
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


    private static int truncate(double d) {
        return d < 0 ? ((int) d - 1) : (int) d;
    }

}

