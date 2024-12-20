package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.registry.Registry;

public class Requirement<T> {

    protected final Check<T> check;
    protected final boolean invert;

    public Requirement(Check<T> check, boolean invert) {
        this.check = check;
        this.invert = invert;
    }

    public boolean check(T t) {
        return this.check.check(t);
    }

    public boolean isInverted() {
        return invert;
    }

    public static <T> Serializer<Requirement<T>> serializer(Registry<?, CheckType<T, ?>> registry) {
        return Check.serializer(registry).dispatch(check ->
                ObjectSerializer.create(Serializer.BOOLEAN.<Requirement<T>>entry("invert", Requirement::isInverted).orElse(false),
                invert -> new Requirement<>(check, invert)),
                req -> req.check);
    }
}
