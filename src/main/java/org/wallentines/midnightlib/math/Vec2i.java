package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

/**
 * A two-dimensional vector of integers
 */
@SuppressWarnings("unused")
public class Vec2i {
    private final int x;
    private final int y;

    /**
     * Constructs a vector from two integers
     * @param x The first value
     * @param y The second value
     */
    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the first (x) value of the vector
     * @return The first value
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the second (y) value of the vector
     * @return The second value
     */
    public int getY() {
        return this.y;
    }

    /**
     * Determines the distance to another two-dimensional vector of integers
     * @param other The other vector
     * @return The distance between the vectors
     */
    public double distance(Vec2i other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Determines the squared distance to another two-dimensional vector of integers
     * @param other The other vector
     * @return The squared distance between the vectors
     */
    public int distanceSquared(Vec2i other) {
        int distX = this.getX() - other.getX();
        int distY = this.getY() - other.getY();
        return distX * distX + distY * distY;
    }


    /**
     * Creates a new vector by adding a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec2i add(int value) {
        return new Vec2i(x + value, y + value);
    }


    /**
     * Creates a new vector by subtracting a value from all values in the vector
     * @param value The value to subtract
     * @return A new vector
     */
    public Vec2i subtract(int value) {
        return new Vec2i(x - value, y - value);
    }


    /**
     * Creates a new vector by multiplying a value by all values in the vector
     * @param value The value to multiply
     * @return A new vector
     */
    public Vec2i multiply(int value) {
        return new Vec2i(x * value, y * value);
    }


    /**
     * Creates a new vector by adding another vector's values to this vector's values
     * @param other The vector to add
     * @return A new vector
     */
    public Vec2i add(Vec2i other) {
        return new Vec2i(x + other.x, y + other.y);
    }


    /**
     * Creates a new vector by subtracting another vector's values to this vector's values
     * @param other The vector to subtract
     * @return A new vector
     */
    public Vec2i subtract(Vec2i other) {
        return new Vec2i(x - other.x, y - other.y);
    }


    /**
     * Creates a new vector by multiplying another vector's values to this vector's values
     * @param other The vector to multiply
     * @return A new vector
     */
    public Vec2i multiply(Vec2i other) {
        return new Vec2i(x * other.x, y * other.y);
    }

    @Override
    public String toString() {
        return getX() + "," + getY();
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


    /**
     * Parses a two-dimensional vector of doubles from a string in the format "x,y"
     * @param str The string to parse
     * @return A parsed vector, or null if the String is not formatted correctly
     */
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

    public static final Serializer<Vec2i> SERIALIZER = InlineSerializer.of(Object::toString, Vec2i::parse).or(
            ObjectSerializer.create(
                    Serializer.INT.entry("x", Vec2i::getX),
                    Serializer.INT.entry("y", Vec2i::getY),
                    Vec2i::new
            ));
}

