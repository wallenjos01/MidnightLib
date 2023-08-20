package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.ConfigObject;
import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.Objects;

/**
 * A serializable object which represents a Predicate for a particular object
 * @param <T> The type of object to check
 */
public class Requirement<T> {

    private final RequirementType<T> type;
    private final ConfigObject config;

    /**
     * Constructs a new Requirement instance with the given type and config
     * @param type The type of requirement
     * @param config The requirement configuration
     */
    public Requirement(RequirementType<T> type, ConfigObject config) {
        this.type = type;
        this.config = config;
    }

    /**
     * Checks whether the given object satisfies the requirement
     * @param data The object to check
     * @return Whether the object satisfies the requirement
     */
    public boolean check(T data) {
        return type.check(data, config, this);
    }

    /**
     * Gets the type of this requirement
     * @return The type of requirement
     */
    public RequirementType<T> getType() {
        return type;
    }

    /**
     * Gets the configuration for this requirement
     * @return The configuration
     */
    public ConfigObject getConfig() {
        return config;
    }

    /**
     * Generates a serializer which can only serialize basic (non-MultiRequirement) requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T> Serializer<Requirement<T>> simpleSerializer(RegistryBase<?, RequirementType<T>> registry) {
        return ObjectSerializer.create(
                registry.nameSerializer().entry("type", Requirement<T>::getType),
                ConfigObject.SERIALIZER.entry("value", Requirement<T>::getConfig),
                Requirement<T>::new
        );
    }

    /**
     * Generates a serializer which can only basic and MultiRequirement requirements from the given registry
     * @param registry The registry of requirement types
     * @return A new serializer
     * @param <T> The type of object checked by the requirement types in the registry
     */
    public static <T> Serializer<Requirement<T>> serializer(RegistryBase<?, RequirementType<T>> registry) {
        return new Serializer<>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context, Requirement<T> value) {

                if(value instanceof MultiRequirement) {
                    return MultiRequirement.multiSerializer(registry).serialize(context, (MultiRequirement<T>) value);
                }

                return simpleSerializer(registry).serialize(context, value);
            }

            @Override
            public <O> SerializeResult<Requirement<T>> deserialize(SerializeContext<O> context, O value) {

                O values = context.get("values", value);
                if(values != null && context.isList(values)) {
                    return MultiRequirement.multiSerializer(registry).deserialize(context, value).flatMap(req -> req);
                }

                return simpleSerializer(registry).deserialize(context, value);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requirement<?> that = (Requirement<?>) o;
        return Objects.equals(type, that.type) && Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, config);
    }
}
