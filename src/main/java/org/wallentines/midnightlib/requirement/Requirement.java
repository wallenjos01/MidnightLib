package org.wallentines.midnightlib.requirement;

import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.config.serialization.ConfigSerializer;
import org.wallentines.midnightlib.config.serialization.InlineSerializer;
import org.wallentines.midnightlib.registry.Identifier;
import org.wallentines.midnightlib.registry.Registry;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.ArrayList;
import java.util.List;

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

    public static class RequirementSerializer<T> implements ConfigSerializer<Requirement<T>> {

        private final RegistryBase<?, RequirementType<T>> registry;

        public RequirementSerializer(RegistryBase<?, RequirementType<T>> registry) {
            this.registry = registry;
        }

        @Override
        public Requirement<T> deserialize(ConfigSection section) {

            if(section.has("values", List.class)) {

                boolean any = section.has("any", Boolean.class) && section.getBoolean("any");

                List<Requirement<T>> reqs = new ArrayList<>();
                for(ConfigSection sec : section.getListFiltered("values", ConfigSection.class)) {
                    reqs.add(deserialize(sec));
                }

                return new MultiRequirement<>(any, reqs);

            } else {

                RequirementType<T> type = registry.nameSerializer().deserialize(section.getString("type"));
                return new Requirement<>(type, section.getString("value"));
            }
        }

        @Override
        public ConfigSection serialize(Requirement<T> object) {

            if(object instanceof MultiRequirement) return MultiRequirement.serialize((MultiRequirement<?>) object);

            return new ConfigSection().with("type", object.type).with("value", object.value);

        }
    }

}
