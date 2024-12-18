package org.wallentines.midnightlib.requirement;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * A serializable object which represents a Predicate for a particular object
 * @param <T> The type of object to check
 */
public class Requirement<V, T extends CheckType<V>> {

    protected final T type;
    protected final Check<V> check;
    protected final boolean invert;

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     */
    public Requirement(Check<V> check) {
        this(null, check, false);
    }

    /**
     * Constructs a non-serializable Requirement instance with the given type and config
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(Check<V> check, boolean invert) {
        this(null, check, invert);
    }

    /**
     * Constructs a new Requirement instance with the given type and config
     * @param type The type of requirement
     * @param check The code used to check the requirement target
     * @param invert Whether to invert the result of the check
     */
    public Requirement(@Nullable T type, Check<V> check, boolean invert) {
        this.type = type;
        this.check = check;
        this.invert = invert;
    }

    /**
     * Checks whether the given object satisfies the requirement
     * @param data The object to check
     * @return Whether the object satisfies the requirement
     */

    public boolean check(V data) {
        return invert ^ check.check(data);
    }

    public boolean isInverted() {
        return invert;
    }


    /**
     * Gets the type of this requirement
     * @return The type of requirement
     */
    public T getType() {
        return type;
    }

    /**
     * Creates a simple requirement using the given check
     * @param check The logic to use to check
     * @return A new requirement
     * @param <T> The type of things to check
     */
    public static <T> Requirement<T, CheckType<T>> simple(Check<T> check) {
        return new Requirement<>(check);
    }

    /**
     * Creates a simple requirement using the given predicate
     * @param predicate The logic to use to check
     * @return A new requirement
     * @param <T> The type of things to check
     */
    public static <T> Requirement<T, CheckType<T>> simple(Predicate<T> predicate) {
        return new Requirement<>(Check.of(predicate));
    }

    /**
     * Creates a composite requirement using the given range and sub-requirements
     * @param range The amount of requirements which must be completed
     * @param requirements The sub-requirements
     * @return A new requirement
     * @param <V> The type of things to check
     * @param <T> The check type
     */
    public static <V, T extends CheckType<V>> Requirement<V, T> composite(Range<Integer> range, Collection<Requirement<V,T>> requirements) {
        return new Requirement<>(new CompositeCheck<>(null, range, requirements));
    }


    /**
     * Generates a serializer which can only serialize requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <V, T extends CheckType<V>> Serializer<Requirement<V,T>> serializer(Registry<?, T> registry) {
        return serializer(registry, Requirement::new);
    }

    /**
     * Generates a serializer which can only serialize requirements from the given registry
     * @param registry The registry of requirement types
     * @param constructor The requirement constructor
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <V, T extends CheckType<V>, R extends Requirement<V,T>> Serializer<R> serializer(Registry<?, T> registry, Functions.F3<T, Check<V>, Boolean, R> constructor) {

        return registry.byIdSerializer().fieldOf("type").dispatch(type -> {
            return new Serializer<R>() {
                @Override
                public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, R requirement) {

                    SerializeResult<O> out = requirement.check.serialize(ctx);
                    if(!out.isComplete()) return out;

                    O map = out.getOrNull();
                    if(!ctx.isMap(map)) return SerializeResult.failure("Check serializer returned invalid result!");

                    if(requirement.invert) ctx.set("invert", ctx.toBoolean(true), map);
                    return SerializeResult.success(map);
                }

                @Override
                public <O> SerializeResult<R> deserialize(SerializeContext<O> ctx, O o) {
                    boolean invert = ctx.asBoolean(ctx.get("invert", o)).getOr(false);
                    return type.deserialize(ctx, o).flatMap(check -> constructor.apply(type, check, invert));
                }
            };
        }, req -> {
            return req.type;
        });

//        return new Serializer<>() {
//            @Override
//            public <O> SerializeResult<O> serialize(SerializeContext<O> context, R value) {
//
//                return value.check.serialize(context).map(o -> {
//                    if (!context.isMap(o)) {
//                        return SerializeResult.failure("Check serializer returned invalid result!");
//                    }
//
//                    SerializeResult<String> type = registry.byIdSerializer().writeString(value.getType());
//                    if(!type.isComplete()) {
//                        return SerializeResult.failure("Unable to serialize type! " + type.getError());
//                    }
//
//                    context.set("type", context.toString(type.getOrNull()), o);
//                    if(value.invert) context.set("invert", context.toBoolean(true), o);
//
//                    return SerializeResult.success(o);
//                });
//            }
//
//            @Override
//            public <O> SerializeResult<R> deserialize(SerializeContext<O> context, O value) {
//
//                if (!context.isMap(value)) {
//                    return SerializeResult.failure("Expected a map!");
//                }
//
//                String str = context.asString(context.get("type", value));
//                if (str == null) {
//                    return SerializeResult.failure("Key type was missing!");
//                }
//
//                Boolean invertNullable = context.asBoolean(context.get("invert", value));
//                boolean invert = invertNullable != null && invertNullable;
//
//                SerializeResult<T> typeRes = registry.byIdSerializer().readString(str);
//                if (!typeRes.isComplete()) {
//                    return SerializeResult.failure("Unable to find serializer for requirement type " + str + "! " + typeRes.getError());
//                }
//
//                T type = typeRes.getOrNull();
//                return type.deserialize(context, value).flatMap(chk -> constructor.apply(type, chk, invert));
//            }
//        };
    }


    public static <T> Registry<Identifier, CheckType<T>> defaultRegistry(String defaultNamespace) {

        Registry<Identifier, CheckType<T>> out = Registry.create(defaultNamespace);
        out.tryRegister("composite", CompositeCheck.type(serializer(out)));
        return out;
    }

}
