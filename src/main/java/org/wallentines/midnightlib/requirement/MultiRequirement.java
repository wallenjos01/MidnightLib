package org.wallentines.midnightlib.requirement;

import org.wallentines.midnightlib.config.ConfigSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    static ConfigSection serialize(MultiRequirement<?> req) {

        return new ConfigSection().with("any", req.any).with("values", req.requirements);
    }

}
