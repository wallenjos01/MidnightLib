package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.registry.RegistryBase;

public class Requirement<T> {

    private final RequirementType<T> type;
    private final String value;

    public Requirement(RequirementType<T> type, String value) {
        this.type = type;
        this.value = value;
    }

    public boolean check(T data) {
        return type.check(data, this, value);
    }

    public RequirementType<T> getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static <T> Serializer<Requirement<T>> simpleSerializer(RegistryBase<?, RequirementType<T>> registry) {
        return ObjectSerializer.create(
                registry.nameSerializer().entry("type", Requirement<T>::getType),
                Serializer.STRING.entry("value", Requirement<T>::getValue),
                Requirement<T>::new
        );
    }

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

}
