package org.wallentines.midnightlib.math;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;

public interface Region {

    /**
     * Determines whether a given point is within the region
     * @param vector The point to check
     * @return Whether the point is within the region
     */
    boolean isWithin(Vec3d vector);

    /**
     * Determines whether a given point is within the region
     * @param vector The point to check
     * @return Whether the point is within the region
     */
    default boolean isWithin(Vec3i vector) {
        return isWithin(new Vec3d(vector.getX(), vector.getY(), vector.getZ()));
    }

    <T> SerializeResult<T> serialize(SerializeContext<T> context);


    Serializer<Region> SERIALIZER = new Serializer<>() {
        @Override
        public <O> SerializeResult<O> serialize(SerializeContext<O> context, Region value) {
            return value.serialize(context);
        }

        @Override
        public <O> SerializeResult<Region> deserialize(SerializeContext<O> context, O value) {

            return CuboidRegion.SERIALIZER.deserialize(context, value)
                    .flatMap(cr -> (Region) cr)
                    .mapError(() -> SphereRegion.SERIALIZER.deserialize(context, value)
                            .flatMap(cr -> cr));
        }
    };

    /**
     * Parses a region from the given string in the format "X0,Y0,Z0;X1,Y1,Z1" or "X,Y,ZrR"
     * @param string The string to parse
     * @return A new region, or null if the string is not in the correct format
     */
    @Nullable
    static Region parse(String string) {

        if(string.lastIndexOf('r') == -1) {
            return CuboidRegion.parse(string);
        }
        return SphereRegion.parse(string);
    }

}
