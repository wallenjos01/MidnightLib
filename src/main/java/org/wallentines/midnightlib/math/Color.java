package org.wallentines.midnightlib.math;

import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;

import java.util.Objects;

@SuppressWarnings("unused")
public class Color {

    private static final Color[] FOUR_BIT_COLORS = new Color[]{new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255)};

    private final int red;
    private final int green;
    private final int blue;
    private int closest4bitColor = -1;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(int rgb) {

        red = rgb >> 16;
        green = (rgb >> 8) - (red << 8);
        blue = rgb - (red << 16) - (green << 8);
    }

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

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public String toHex() {
        return "#" + toPlainHex();
    }

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

    public int toDecimal() {
        return (red << 16) + (green << 8) + blue;
    }

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

    public static Color fromRGBI(int value) {
        return FOUR_BIT_COLORS[value];
    }

    public static Color parse(String s) {

        return new Color(s);
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

    public double getDistanceTo(Color c) {
        return getDistance(this, c);
    }

    public double getDistanceSquaredTo(Color c) {
        return getDistanceSquared(this, c);
    }

    public static double getDistance(Color c1, Color c2) {
        return Math.sqrt(getDistanceSquared(c1, c2));
    }

    public static double getDistanceSquared(Color c1, Color c2) {
        int r = c2.red - c1.red;
        int g = c2.green - c1.green;
        int b = c2.blue - c1.blue;
        return r * r + g * g + b * b;
    }

    public Color multiply(double multiplier) {
        return new Color((int) (red * multiplier), (int) (green * multiplier), (int) (blue * multiplier));
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

