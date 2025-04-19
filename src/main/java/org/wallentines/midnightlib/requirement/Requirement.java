package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.mdcfg.registry.Registry;

import java.util.function.BiFunction;

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
        return serializer(registry, Requirement::new);
    }

    public static <T, R extends Requirement<T>> Serializer<R> serializer(Registry<?, CheckType<T, ?>> registry, BiFunction<Check<T>, Boolean, R> constructor) {
        return Check.serializer(registry).dispatch(check ->
                        ObjectSerializer.create(Serializer.BOOLEAN.entry("invert", R::isInverted).orElse(false),
                                invert -> constructor.apply(check, invert)),
                req -> req.check);
    }

}
