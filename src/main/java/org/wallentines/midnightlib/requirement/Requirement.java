package org.wallentines.midnightlib.requirement;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

/**
 * A serializable object which represents a Predicate for a particular object
 * @param <T> The type of object to check
 */
public class Requirement<T, P extends Predicate<T>> {

    protected final Serializer<P> serializer;
    protected final P check;
    protected final boolean invert;

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     */
    public Requirement(P check) {
        this(null, check, false);
    }

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(P check, boolean invert) {
        this(null, check, invert);
    }

    /**
     * Constructs a new Requirement instance with the given type and config
     * @param serializer The type of requirement
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(@Nullable Serializer<P> serializer, P check, boolean invert) {
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
    public Serializer<P> getSerializer() {
        return serializer;
    }

    public static <T> Requirement<T, Predicate<T>> simple(Predicate<T> pred) {
        return new Requirement<>(pred);
    }

    public static <T> Requirement<T, Predicate<T>> composite(Range<Integer> range, Collection<Requirement<T, Predicate<T>>> pred) {
        return new Requirement<>(new CompositeCheck<>(range, pred));
    }

    /**
     * Generates a serializer which can only serialize requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T, P extends Predicate<T>> Serializer<Requirement<T, P>> serializer(RegistryBase<?, Serializer<P>> registry) {
        return serializer(registry, Requirement::new);
    }

    /**
     * Generates a serializer which can only serialize requirements from the given registry
     * @param registry The registry of requirement types
     * @param constructor The constructor to use to build the requirement
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T, P extends Predicate<T>, R extends Requirement<T, P>> Serializer<R> serializer(RegistryBase<?, Serializer<P>> registry, Functions.F3<Serializer<P>, P, Boolean, R> constructor) {
        return new Serializer<>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context, R value) {

                return value.serialize(context).flatMap(o -> {
                    O out = context.toMap(new HashMap<>());
                    context.set("type", context.toString(registry.nameSerializer().writeString(value.getSerializer())), out);
                    context.set("value", o, out);
                    if(value.invert) context.set("invert", context.toBoolean(true), out);
                    return out;
                });
            }

            @Override
            public <O> SerializeResult<R> deserialize(SerializeContext<O> context, O value) {

                if (!context.isMap(value)) {
                    return SerializeResult.failure("Expected a map!");
                }

                String str = context.asString(context.get("type", value));
                if (str == null) {
                    return SerializeResult.failure("Key type was missing!");
                }

                Boolean invertNullable = context.asBoolean(context.get("invert", value));
                boolean invert = invertNullable != null && invertNullable;

                Serializer<P> ser = registry.nameSerializer().readString(str);
                if (ser == null) {
                    return SerializeResult.failure("Unable to find serializer for requirement type " + str + "!");
                }

                return ser.deserialize(context, value).flatMap(pr -> constructor.apply(ser, pr, invert));
            }
        };
    }


    public static <T> Registry<Serializer<Predicate<T>>> defaultRegistry(String defaultNamespace) {

        Registry<Serializer<Predicate<T>>> out = new Registry<>(defaultNamespace);
        out.register("composite", CompositeCheck.serializer(serializer(out)));
        return out;
    }

}
