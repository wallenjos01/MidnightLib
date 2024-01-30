package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.*;
import java.util.function.Function;

public class StringRequirement<T> extends Requirement<T> {

    public static <T> RequirementType<T> type(Function<T, String> getter) {
        return type(getter, StringRequirement::new);
    }

    public static <T> RequirementType<T> type(Function<T, String> getter, Functions.F4<RequirementType<T>, Boolean, Function<T, String>, Collection<String>, Requirement<T>> builder) {
        return new RequirementType<>() {
            @Override
            public <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value, boolean invert) {
                return SerializeResult.ofNullable(ctx.asString(value)).flatMap(str -> builder.apply(this, invert, getter, List.of(str))).mapError(() ->
                        SerializeResult.ofNullable(ctx.asList(value), "Expected a String or a list!").flatMap(lst -> {
                            List<String> out = new ArrayList<>();
                            for(C c : lst) {
                                out.add(ctx.asString(c));
                            }
                            return builder.apply(this, invert, getter, out);
                        })
                );
            }
        };
    }

    private final Function<T, String> getter;
    private final Set<String> values;

    public StringRequirement(RequirementType<T> type, boolean invert, Function<T, String> getter, String value) {
        super(type, invert);
        this.getter = getter;
        this.values = Set.of(value);
    }

    public StringRequirement(RequirementType<T> type, boolean invert, Function<T, String> getter, Collection<String> values) {
        super(type, invert);
        this.getter = getter;
        this.values = Set.copyOf(values);
    }

    @Override
    protected boolean doCheck(T data) {
        return values.contains(getter.apply(data));
    }

    @Override
    public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {
        return SerializeResult.success(ctx.toList(values.stream().map(ctx::toString).toList()));
    }
}
