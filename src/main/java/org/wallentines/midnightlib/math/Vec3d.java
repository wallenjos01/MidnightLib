package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

/**
 * A three-dimensional vector of doubles
 */
public class Vec3d {
    private final double x;
    private final double y;
    private final double z;

    /**
     * Constructs a vector from three doubles
     * @param x The first value
     * @param y The second value
     * @param z The third value
     */
    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
     * Gets the third (z) value of the vector
     * @return The third value
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Determines the distance to another three-dimensional vector of doubles
     * @param other The other vector
     * @return The distance between the vectors
     */
    public double distance(Vec3d other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Determines the squared distance to another three-dimensional vector of doubles
     * @param other The other vector
     * @return The squared distance between the vectors
     */
    public double distanceSquared(Vec3d other) {
        double ax = this.getX() - other.getX();
        double ay = this.getY() - other.getY();
        double az = this.getZ() - other.getZ();
        return ax * ax + ay * ay + az * az;
    }


    /**
     * Converts the vector into a three-dimensional vector of integers by truncating its values
     * @return A truncated vector
     */
    public Vec3i truncate() {

        return new Vec3i(truncate(x), truncate(y), truncate(z));
    }


    /**
     * Creates a new vector by adding a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec3d add(double value) {
        return new Vec3d(x + value, y + value, z + value);
    }


    /**
     * Creates a new vector by subtracting a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec3d subtract(double value) {
        return new Vec3d(x - value, y - value, z - value);
    }


    /**
     * Creates a new vector by multiplying a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec3d multiply(double value) {
        return new Vec3d(x * value, y * value, z * value);
    }

    /**
     * Creates a new vector by adding another vector's values to this vector's values
     * @param other The vector to add
     * @return A new vector
     */
    public Vec3d add(Vec3d other) {
        return new Vec3d(x + other.x, y + other.y, z + other.z);
    }


    /**
     * Creates a new vector by subtracting another vector's values to this vector's values
     * @param other The vector to subtract
     * @return A new vector
     */
    public Vec3d subtract(Vec3d other) {
        return new Vec3d(x - other.x, y - other.y, z - other.z);
    }


    /**
     * Creates a new vector by multiplying another vector's values to this vector's values
     * @param other The vector to multiply
     * @return A new vector
     */
    public Vec3d multiply(Vec3d other) {
        return new Vec3d(x * other.x, y * other.y, z * other.z);
    }


    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3d)) {
            return false;
        }
        Vec3d other = (Vec3d)obj;
        return other.getX() == this.getX() && other.getY() == this.getY() && other.getZ() == this.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }


    /**
     * Parses a two-dimensional vector of doubles from a string in the format "x,y,z"
     * @param str The string to parse
     * @return A parsed vector, or null if the String is not formatted correctly
     */
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

    public static final Serializer<Vec3d> SERIALIZER = InlineSerializer.of(Object::toString, Vec3d::parse).or(
        ObjectSerializer.create(
            Serializer.DOUBLE.entry("x", Vec3d::getX),
            Serializer.DOUBLE.entry("y", Vec3d::getY),
            Serializer.DOUBLE.entry("z", Vec3d::getZ),
            Vec3d::new
        ));


    private static int truncate(double d) {
        return d < 0 ? ((int) d - 1) : (int) d;
    }

}