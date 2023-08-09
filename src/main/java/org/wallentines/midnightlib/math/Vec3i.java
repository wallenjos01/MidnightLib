package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

/**
 * A three-dimensional vector of integers
 */
public class Vec3i {
    private final int x;
    private final int y;
    private final int z;

    /**
     * Constructs a vector from three integers
     * @param x The first value
     * @param y The second value
     * @param z The third value
     */
    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
     * Gets the third (z) value of the vector
     * @return The third value
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Determines the distance to another three-dimensional vector of integers
     * @param other The other vector
     * @return The distance between the vectors
     */
    public double distance(Vec3i other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Determines the squared distance to another three-dimensional vector of integers
     * @param other The other vector
     * @return The squared distance between the vectors
     */
    public int distanceSquared(Vec3i other) {
        int distX = this.getX() - other.getX();
        int distY = this.getY() - other.getY();
        int distZ = this.getZ() - other.getZ();
        return distX * distX + distY * distY + distZ * distZ;
    }


    /**
     * Creates a new vector by adding a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec3i add(int value) {
        return new Vec3i(x + value, y + value, z + value);
    }


    /**
     * Creates a new vector by subtracting a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec3i subtract(int value) {
        return new Vec3i(x - value, y - value, z - value);
    }


    /**
     * Creates a new vector by multiplying a value to all values in the vector
     * @param value The value to add
     * @return A new vector
     */
    public Vec3i multiply(int value) {
        return new Vec3i(x * value, y * value, z * value);
    }


    /**
     * Creates a new vector by adding another vector's values to this vector's values
     * @param other The vector to add
     * @return A new vector
     */
    public Vec3i add(Vec3i other) {
        return new Vec3i(x + other.x, y + other.y, z + other.z);
    }


    /**
     * Creates a new vector by subtracting another vector's values to this vector's values
     * @param other The vector to subtract
     * @return A new vector
     */
    public Vec3i subtract(Vec3i other) {
        return new Vec3i(x - other.x, y - other.y, z - other.z);
    }


    /**
     * Creates a new vector by multiplying another vector's values to this vector's values
     * @param other The vector to multiply
     * @return A new vector
     */
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


    /**
     * Parses a two-dimensional vector of doubles from a string in the format "x,y,z"
     * @param str The string to parse
     * @return A parsed vector, or null if the String is not formatted correctly
     */
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

    public static final Serializer<Vec3i> SERIALIZER = InlineSerializer.of(Object::toString, Vec3i::parse).or(
        ObjectSerializer.create(
            Serializer.INT.entry("x", Vec3i::getX),
            Serializer.INT.entry("y", Vec3i::getY),
            Serializer.INT.entry("z", Vec3i::getZ),
            Vec3i::new
        ));
}

