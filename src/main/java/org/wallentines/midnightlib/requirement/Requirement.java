package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.*;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.HashMap;

/**
 * A serializable object which represents a Predicate for a particular object
 * @param <T> The type of object to check
 */
public abstract class Requirement<T> {

    private final RequirementType<T> type;

    /**
     * Constructs a new Requirement instance with the given type and config
     * @param type The type of requirement
     */
    protected Requirement(RequirementType<T> type) {
        this.type = type;
    }

    /**
     * Checks whether the given object satisfies the requirement
     * @param data The object to check
     * @return Whether the object satisfies the requirement
     */
    public abstract boolean check(T data);


    public abstract <C> SerializeResult<C> serialize(SerializeContext<C> ctx);

    /**
     * Gets the type of this requirement
     * @return The type of requirement
     */
    public RequirementType<T> getType() {
        return type;
    }


    /**
     * Generates a serializer which can only serialize basic (non-MultiRequirement) requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T> Serializer<Requirement<T>> serializer(RegistryBase<?, RequirementType<T>> registry) {
        return new Serializer<>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context, Requirement<T> value) {
                return value.serialize(context).flatMap(o -> {
                    O out = context.toMap(new HashMap<>());
                    context.set("type", context.toString(registry.nameSerializer().writeString(value.type)), out);
                    context.set("value", o, out);
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

                RequirementType<T> req = registry.nameSerializer().readString(str);
                if (req == null) {
                    return SerializeResult.failure("Unable to find requirement type " + str + "!");
                }

                return req.create(context, context.get("value", value));
            }
        };
    }
}
