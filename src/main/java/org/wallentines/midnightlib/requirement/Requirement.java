package org.wallentines.midnightlib.requirement;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * A serializable object which represents a Predicate for a particular object
 * @param <T> The type of object to check
 */
public class Requirement<T> {

    protected final CheckType<T> type;
    protected final Check<T> check;
    protected final boolean invert;

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     */
    public Requirement(Check<T> check) {
        this(null, check, false);
    }

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(Check<T> check, boolean invert) {
        this(null, check, invert);
    }

    /**
     * Constructs a new Requirement instance with the given type and config
     * @param type The type of requirement
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(@Nullable CheckType<T> type, Check<T> check, boolean invert) {
        this.type = type;
        this.check = check;
        this.invert = invert;
    }

    /**
     * Checks whether the given object satisfies the requirement
     * @param data The object to check
     * @return Whether the object satisfies the requirement
     */

    public boolean check(T data) {
        return invert ^ check.check(data);
    }

    public boolean isInverted() {
        return invert;
    }


    /**
     * Gets the type of this requirement
     * @return The type of requirement
     */
    public CheckType<T> getType() {
        return type;
    }

    /**
     * Creates a simple requirement using the given check
     * @param check The logic to use to check
     * @return A new requirement
     * @param <T> The type of things to check
     */
    public static <T> Requirement<T> simple(Check<T> check) {
        return new Requirement<>(check);
    }

    /**
     * Creates a simple requirement using the given predicate
     * @param predicate The logic to use to check
     * @return A new requirement
     * @param <T> The type of things to check
     */
    public static <T> Requirement<T> simple(Predicate<T> predicate) {
        return new Requirement<>(Check.of(predicate));
    }

    /**
     * Creates a composite requirement using the given range and sub-requirements
     * @param range The amount of requirements which must be completed
     * @param requirements The sub-requirements
     * @return A new requirement
     * @param <T> The type of things to check
     */
    public static <T> Requirement<T> composite(Range<Integer> range, Collection<Requirement<T>> requirements) {
        return new Requirement<>(new CompositeCheck<>(null, range, requirements));
    }

    /**
     * Generates a serializer which can only serialize requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T> Serializer<Requirement<T>> serializer(RegistryBase<?, CheckType<T>> registry) {

        return new Serializer<>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context, Requirement<T> value) {

                return value.check.serialize(context).map(o -> {
                    if (!context.isMap(o)) {
                        return SerializeResult.failure("Check serializer returned invalid result!");
                    }

                    context.set("type", context.toString(registry.nameSerializer().writeString(value.getType())), o);
                    if(value.invert) context.set("invert", context.toBoolean(true), o);

                    return SerializeResult.success(o);
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

                CheckType<T> type = registry.nameSerializer().readString(str);
                if (type == null) {
                    return SerializeResult.failure("Unable to find serializer for requirement type " + str + "!");
                }

                return type.deserialize(context, value).flatMap(pr -> new Requirement<>(type, pr, invert));
            }
        };
    }


    public static <T> Registry<CheckType<T>> defaultRegistry(String defaultNamespace) {

        Registry<CheckType<T>> out = new Registry<>(defaultNamespace);
        out.register("composite", CompositeCheck.type(serializer(out)));
        return out;
    }

}
