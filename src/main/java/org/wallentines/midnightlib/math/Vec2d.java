package org.wallentines.midnightlib.math;

import org.wallentines.midnightlib.config.serialization.InlineSerializer;

public class Vec2d {
    private final double[] data;

    public Vec2d(double x, double y) {
        this.data = new double[]{x, y};
    }

    public double getX() {
        return this.data[0];
    }

    public double getY() {
        return this.data[1];
    }


    public double distance(Vec2d vec2) {
        double ax = this.getX() - vec2.getX();
        double ay = this.getY() - vec2.getY();
        return Math.sqrt(ax * ax + ay * ay);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2d)) {
            return false;
        }
        Vec2d other = (Vec2d)obj;
        return other.getX() == this.getX() && other.getY() == this.getY();
    }

    private static int truncate(double d) {
        return d < 0 ? ((int) d - 1) : (int) d;
    }

    public Vec2i truncate() {

        return new Vec2i(truncate(data[0]), truncate(data[1]));
    }

    @Override
    public String toString() {
        return getX() + "," + getY();
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

    public Vec2d add(double i) {
        return new Vec2d(data[0] + i, data[1] + i);
    }

    public Vec2d multiply(double i) {
        return new Vec2d(data[0] * i, data[1] * i);
    }

    public Vec2d add(Vec2d i) {
        return new Vec2d(data[0] + i.data[0], data[1] + i.data[1]);
    }

    public Vec2d multiply(Vec2d i) {
        return new Vec2d(data[0] * i.data[0], data[1] * i.data[1]);
    }


    public static final InlineSerializer<Vec2d> SERIALIZER = new InlineSerializer<Vec2d>() {
        @Override
        public Vec2d deserialize(String s) {
            return parse(s);
        }

        @Override
        public String serialize(Vec2d object) {
            return object.toString();
        }

        @Override
        public boolean canDeserialize(String s) {
            if(s != null && s.contains(",")) {

                String[] ss = s.split(",");
                if(ss.length < 2) return false;

                try {
                    for(int i = 0 ; i < 2 ; i++) {
                        Double.parseDouble(ss[i]);
                    }

                } catch (NumberFormatException ex) {
                    return false;
                }
                return true;
            }
            return false;
        }
    };


}

