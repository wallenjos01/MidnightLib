package org.wallentines.midnightlib.requirement;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.HashMap;
import java.util.function.Predicate;

/**
 * A serializable object which represents a Predicate for a particular object
 * @param <T> The type of object to check
 */
public class Requirement<T> {

    private final Serializer<Predicate<T>> serializer;
    private final Predicate<T> check;
    private final boolean invert;

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     */
    public Requirement(Predicate<T> check) {
        this(null, check, false);
    }

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(Predicate<T> check, boolean invert) {
        this(null, check, invert);
    }

    /**
     * Constructs a new Requirement instance with the given type and config
     * @param serializer The type of requirement
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(@Nullable Serializer<Predicate<T>> serializer, Predicate<T> check, boolean invert) {
        this.serializer = serializer;
        this.check = check;
        this.invert = invert;
    }

    /**
     * Checks whether the given object satisfies the requirement
     * @param data The object to check
     * @return Whether the object satisfies the requirement
     */

    public boolean check(T data) {
        return invert ^ check.test(data);
    }

    public boolean isInverted() {
        return invert;
    }

    public boolean isSerializable() {
        return serializer != null;
    }

    public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {
        if(serializer == null) {
            return SerializeResult.failure("This requirement is not serializable!");
        }
        return serializer.serialize(ctx, check);
    }

    /**
     * Gets the type of this requirement
     * @return The type of requirement
     */
    public Serializer<Predicate<T>> getSerializer() {
        return serializer;
    }


    /**
     * Generates a serializer which can only serialize basic (non-MultiRequirement) requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T> Serializer<Requirement<T>> serializer(RegistryBase<?, Serializer<Predicate<T>>> registry) {
        return new Serializer<>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context, Requirement<T> value) {

                return value.serialize(context).flatMap(o -> {
                    O out = context.toMap(new HashMap<>());
                    context.set("type", context.toString(registry.nameSerializer().writeString(value.serializer)), out);
                    context.set("value", o, out);
                    if(value.invert) context.set("invert", context.toBoolean(true), out);
                    return out;
                });
            }

            @Override
            public <O> SerializeResult<Requirement<T>> deserialize(SerializeContext<O> context, O value) {

                if (!context.isMap(value)) {
                    return SerializeResult.failure("Expected a map!");
                }
                String str = context.asString(context.get("type", value));
                if (str == null) {
                    return SerializeResult.failure("Key type was missing!");
                }

                Boolean invertNullable = context.asBoolean(context.get("invert", value));
                boolean invert = invertNullable != null && invertNullable;

                Serializer<Predicate<T>> ser = registry.nameSerializer().readString(str);
                if (ser == null) {
                    return SerializeResult.failure("Unable to find serializer for requirement type " + str + "!");
                }

                return ser.deserialize(context, context.get("value", value)).flatMap(pr -> new Requirement<>(ser, pr, invert));
            }
        };
    }
}
