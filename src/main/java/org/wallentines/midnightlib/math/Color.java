package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

/**
 * A class which represents an RGB color
 */
@SuppressWarnings("unused")
public class Color {

    // An array of 4-bit (RGBI) colors with RGB values.
    private static final Color[] FOUR_BIT_COLORS = new Color[]{new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255)};

    private final int red;
    private final int green;
    private final int blue;
    private int closest4bitColor = -1;

    /**
     * Constructs a new color with the given values
     * @param red The red channel value
     * @param green The green channel value
     * @param blue The blue channel value
     */
    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Constructs a new color from the packed RGB value
     * @param rgb A packed RGB value (i.e. 0xRRGGBB)
     */
    public Color(int rgb) {

        red = rgb >> 16;
        green = (rgb >> 8) - (red << 8);
        blue = rgb - (red << 16) - (green << 8);
    }

    /**
     * Parses an RGB color from the given hex-code
     * @param name The hex code to parse (i.e. "#RRGGBB")
     */
    public Color(String name) {
        int b;
        int g;
        int r;
        try {
            if (name.startsWith("#")) {
                name = name.substring(1);
            }
            if (name.length() != 6) {
                throw new IllegalStateException("'" + name + "' cannot be converted to a color!");
            }
            r = Integer.parseInt(name.substring(0, 2), 16);
            g = Integer.parseInt(name.substring(2, 4), 16);
            b = Integer.parseInt(name.substring(4, 6), 16);
        }
        catch (NumberFormatException ex) {
            r = 255;
            g = 255;
            b = 255;
        }
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    /**
     * Gets the red channel value
     * @return The red channel value
     */
    public int getRed() {
        return red;
    }

    /**
     * Gets the green channel value
     * @return The green channel value
     */
    public int getGreen() {
        return green;
    }

    /**
     * Gets the blue channel value
     * @return The blue channel value
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Returns the hex code representation of the color
     * @return A hex code (i.e. "#RRGGBB")
     */
    public String toHex() {
        return "#" + toPlainHex();
    }

    /**
     * Returns the hex code representation of the color without the leading '#'
     * @return A plain hex code (i.e. "RRGGBB")
     */
    public String toPlainHex() {
        String r = Integer.toHexString(this.red);
        String g = Integer.toHexString(this.green);
        String b = Integer.toHexString(this.blue);
        if (r.length() == 1) {
            r = "0" + r;
        }
        if (g.length() == 1) {
            g = "0" + g;
        }
        if (b.length() == 1) {
            b = "0" + b;
        }
        return r + g + b;
    }

    /**
     * Returns the packed RGB value representation of the color
     * @return A packed RGB value (i.e. 0xRRGGBB)
     */
    public int toDecimal() {
        return (red << 16) + (green << 8) + blue;
    }

    /**
     * Finds the closest 4-bit RGBI color value to this color
     * @return The closest RGBI value
     */
    public int toRGBI() {

        if (this.closest4bitColor == -1) {

            int out = 0;
            double lowest = getDistanceSquaredTo(FOUR_BIT_COLORS[0]);

            for (int i = 1; i < FOUR_BIT_COLORS.length; ++i) {

                double distance = getDistanceSquaredTo(FOUR_BIT_COLORS[i]);
                if (!(distance < lowest)) continue;
                lowest = distance;
                out = i;
            }

            this.closest4bitColor = out;
        }

        return this.closest4bitColor;
    }

    @Override
    public String toString() {
        return toHex();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Color)) return false;

        Color c = (Color) obj;

        return c.red == red && c.green == green && c.blue == blue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }

    /**
     * Determines the distance to another color
     * @param color The color to compare with
     * @return The distance to the other color
     */
    public double getDistanceTo(Color color) {
        return getDistance(this, color);
    }

    /**
     * Determines the squared distance to another color
     * @param color The color to compare with
     * @return The squared distance to the other color
     */
    public double getDistanceSquaredTo(Color color) {
        return getDistanceSquared(this, color);
    }

    /**
     * Multiplies the all the channels of the color by a given value
     * @param multiplier The value to multiply the channels by
     * @return A new color
     */
    public Color multiply(double multiplier) {
        return new Color((int) (red * multiplier), (int) (green * multiplier), (int) (blue * multiplier));
    }

    /**
     * Determines the distance between two colors
     * @param c1 The first color
     * @param c2 The second color
     * @return The distance between the two colors
     */
    public static double getDistance(Color c1, Color c2) {
        return Math.sqrt(getDistanceSquared(c1, c2));
    }

    /**
     * Determines the squared distance between two colors
     * @param c1 The first color
     * @param c2 The second color
     * @return The squared distance between the two colors
     */
    public static double getDistanceSquared(Color c1, Color c2) {
        int r = c2.red - c1.red;
        int g = c2.green - c1.green;
        int b = c2.blue - c1.blue;
        return r * r + g * g + b * b;
    }

    /**
     * Gets the RGB Color representation of the given RGBI color index
     * @param value The RGBI color index (0x0 -> 0xF)
     * @return The RGB Color representation
     */
    public static Color fromRGBI(int value) {
        return FOUR_BIT_COLORS[value];
    }

    /**
     * Parses a hex color code as a Color
     * @param hex The hex code to parse
     * @return A parsed color
     */
    public static Color parse(String hex) {

        return new Color(hex);
    }

    public static final Color WHITE = new Color(16777215);

    public static final Serializer<Color> SERIALIZER =
            InlineSerializer.of(Color::toHex, Color::new).or(
            ObjectSerializer.create(
                    Serializer.INT.entry("red", Color::getRed),
                    Serializer.INT.entry("green", Color::getGreen),
                    Serializer.INT.entry("blue", Color::getBlue),
                    Color::new
            )
    );
}

