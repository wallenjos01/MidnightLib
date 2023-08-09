package org.wallentines.midnightlib.requirement;


import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MultiRequirement<T> extends Requirement<T> {

    private final boolean any;
    private final List<Requirement<T>> requirements;

    public MultiRequirement(boolean any, Collection<Requirement<T>> requirements) {
        super(null, null);
        this.any = any;
        this.requirements = new ArrayList<>(requirements);
    }

    @Override
    public boolean check(T context) {

        for(Requirement<T> req : requirements) {
            if(req.check(context) == any) return any;
        }

        return false;
    }

    public boolean allowAny() {
        return any;
    }

    public List<Requirement<T>> getRequirements() {
        return requirements;
    }

    public static <T> Serializer<MultiRequirement<T>> multiSerializer(RegistryBase<?, RequirementType<T>> registry) {

        return ObjectSerializer.create(
                Serializer.BOOLEAN.entry("any", req -> req.any),
                Requirement.serializer(registry).listOf().entry("values", req -> req.requirements),
                MultiRequirement::new
        );

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiRequirement<?> that = (MultiRequirement<?>) o;
        return any == that.any && Objects.equals(requirements, that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(any, requirements);
    }
}
