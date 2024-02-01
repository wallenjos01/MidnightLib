package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.math.Range;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.*;
import java.util.function.Predicate;

/**
 * A requirement which contains one or more sub-requirements and an operation to determine how many need to be completed
 * @param <T> The type of object which the requirement applies to
 */
@Deprecated
public class MultiRequirement<T, P extends Predicate<T>> extends Requirement<T, P> {

    private final Range<Integer> op;
    private final RegistryBase<?, Serializer<P>> registry;
    private final List<Requirement<T, P>> requirements;

    /**
     * Constructs a new multi requirement with the given operation and collection of sub-requirements
     * @param op The operation
     * @param invert Whether the result should be inverted
     * @param requirements The number of sub-requirements
     */
    public MultiRequirement(Range<Integer> op, boolean invert, RegistryBase<?, Serializer<P>> registry, Collection<Requirement<T, P>> requirements) {
        super(null, invert);
        this.op = op;
        this.registry = registry;
        this.requirements = List.copyOf(requirements);
    }

    /**
     * Constructs a new multi requirement with the given operation and collection of sub-requirements
     * @param op The operation
     * @param requirements The number of sub-requirements
     */
    public MultiRequirement(Range<Integer> op, RegistryBase<?, Serializer<P>> registry, Collection<Requirement<T, P>> requirements) {
        this(op, false, registry, requirements);
    }


    @Override
    public boolean check(T context) {

        int completed = 0;
        for(Requirement<T, P> req : requirements) {
            if(req.check(context)) {
                completed++;
            }
        }

        boolean result = op instanceof Range.All ? completed == requirements.size() : op.isWithin(completed);
        return isInverted() ^ result;
    }

    @Override
    public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {

        Map<String, C> out = new HashMap<>();

        if(op instanceof Range.Exact && op.isWithin(requirements.size())) {
            out.put("count", ctx.toString("all"));
        } else {
            SerializeResult<C> res = Range.INTEGER.serialize(ctx, op);
            if(!res.isComplete()) {
                return SerializeResult.failure("Failed to serialize a requirement! " + res.getError());
            }
            out.put("count", res.getOrThrow());
        }

        List<C> entries = new ArrayList<>();
        for(Requirement<T, P> req : requirements) {
            SerializeResult<C> reqc = Requirement.serializer(registry).serialize(ctx, req);
            if(!reqc.isComplete()) {
                return SerializeResult.failure("Failed to serialize a requirement! " + reqc.getError());
            }
            entries.add(reqc.getOrThrow());
        }

        if(isInverted()) {
            out.put("inverted", ctx.toBoolean(true));
        }

        out.put("entries", ctx.toList(entries));
        return SerializeResult.success(ctx.toMap(out));
    }

    /**
     * Gets the list of sub-requirements
     * @return A list of requirements
     */
    public List<Requirement<T, P>> getRequirements() {
        return requirements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiRequirement<?, ?> that = (MultiRequirement<?, ?>) o;
        return Objects.equals(op, that.op) && Objects.equals(requirements, that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, requirements);
    }



    /**
     * Creates a serializer which can only serialize MultiRequirement requirements
     * @param registry The registry to find requirement types in
     * @return A new serializer
     * @param <T> The type of data to check in requirements
     */
    public static <T, P extends Predicate<T>> Serializer<MultiRequirement<T, P>> multiSerializer(RegistryBase<?, Serializer<P>> registry) {
        return new Serializer<MultiRequirement<T, P>>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> ctx, MultiRequirement<T, P> req) {
                return req.serialize(ctx);
            }

            @Override
            public <O> SerializeResult<MultiRequirement<T, P>> deserialize(SerializeContext<O> ctx, O o) {

                return Requirement.serializer(registry).listOf().deserialize(ctx, ctx.get("entries", o)).map(entries -> {
                    SerializeResult<Range<Integer>> res = Range.INTEGER.deserialize(ctx, ctx.get("count", o));
                    if(!res.isComplete()) {
                        return SerializeResult.failure("Unable to parse range! " + res.getError());
                    }
                    Range<Integer> count = res.getOrThrow();
                    Boolean invertNullable = ctx.asBoolean(ctx.get("invert", o));
                    boolean invert = invertNullable != null && invertNullable;
                    return SerializeResult.success(new MultiRequirement<>(count, invert, registry, entries));
                });
            }
        };
    };
}
