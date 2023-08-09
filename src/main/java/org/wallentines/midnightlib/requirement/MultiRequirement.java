package org.wallentines.midnightlib.requirement;


import org.wallentines.mdcfg.serializer.ObjectSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;
import org.wallentines.mdcfg.serializer.Serializer;
import org.wallentines.midnightlib.registry.RegistryBase;

import java.util.*;

/**
 * A requirement which contains one or more sub-requirements and an operation to determine how many need to be completed
 * @param <T> The type of object which the requirement applies to
 */
@SuppressWarnings("unused")
public class MultiRequirement<T> extends Requirement<T> {

    private final Operation op;
    private final List<Requirement<T>> requirements;

    /**
     * Constructs a new multi requirement with the given operation and collection of sub-requirements
     * @param op The operation
     * @param requirements The number of sub-requirements
     */
    public MultiRequirement(Operation op, Collection<Requirement<T>> requirements) {
        super(null, null);
        this.op = op;
        this.requirements = new ArrayList<>(requirements);
    }

    @Override
    public boolean check(T context) {

        int completed = 0;
        for(Requirement<T> req : requirements) {
            if(req.check(context)) {
                completed++;
                if(op.check(completed, requirements.size())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the list of sub-requirements
     * @return A list of requirements
     */
    public List<Requirement<T>> getRequirements() {
        return requirements;
    }

    /**
     * Creates a serializer which can only serialize MultiRequirement requirements
     * @param registry The registry to find requirement types in
     * @return A new serializer
     * @param <T> The type of data to check in requirements
     */
    public static <T> Serializer<MultiRequirement<T>> multiSerializer(RegistryBase<?, RequirementType<T>> registry) {

        return ObjectSerializer.create(
                Operation.SERIALIZER.entry("operation", req -> req.op),
                Requirement.serializer(registry).listOf().entry("values", req -> req.requirements),
                MultiRequirement::new
        );

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiRequirement<?> that = (MultiRequirement<?>) o;
        return Objects.equals(op, that.op) && Objects.equals(requirements, that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, requirements);
    }

    /**
     * A class which defines an operation to determine how many requirements are needed to complete a multi-requirement
     */
    public static class Operation {

        private final int minimum;
        private final int maximum;

        private Operation(int minimum, int maximum) {
            this.minimum = minimum;
            this.maximum = maximum;
        }

        private boolean check(int amount, int total) {

            // If the maximum is less than the minimum, assume the total must be achieved
            if(minimum > maximum) {
                return amount >= total;
            }

            return amount >= minimum && amount <= maximum;
        }

        /**
         * Allows a multi-requirement to be completed if any of the sub-requirements are completed
         */
        public static final Operation ANY = new Operation(1, Integer.MAX_VALUE);

        /**
         * Allows a multi-requirement to be completed only if all the sub-requirements are completed
         */
        public static final Operation ALL = new Operation(Integer.MAX_VALUE, -1);

        /**
         * Allows a multi-requirement to be completed if at least a given number of the sub-requirements are completed
         * @param count The minimum number of requirements which need to be completed
         * @return A new Operation
         */
        public static Operation atLeast(int count) {
            return new Operation(count, Integer.MAX_VALUE);
        }

        /**
         * Allows a multi-requirement to be completed if less than a given number of the sub-requirements are completed
         * @param count The maximum number of requirements which can to be completed
         * @return A new Operation
         */
        public static Operation atMost(int count) {
            return new Operation(0, count);
        }

        /**
         * Allows a multi-requirement to be completed if less than a given number of the sub-requirements are completed
         * @param minimum The minimum number of requirements which need to be completed
         * @param maximum The maximum number of requirements which can to be completed
         * @return A new Operation
         */
        public static Operation between(int minimum, int maximum) {
            if(minimum > maximum) throw new IllegalStateException("Minimum bound cannot be greater than maximum bound!");
            return new Operation(minimum, maximum);
        }

        /**
         * Allows a multi-requirement to be completed only if exactly the given number of the sub-requirements are completed
         * @param amount The exact number of requirements which need to and can be completed
         * @return A new Operation
         */
        public static Operation exactly(int amount) {
            return new Operation(amount, amount);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Operation operation = (Operation) o;
            return minimum == operation.minimum && maximum == operation.maximum;
        }

        @Override
        public int hashCode() {
            return Objects.hash(minimum, maximum);
        }

        private static final Serializer<Operation> SERIALIZER = new Serializer<>() {
            @Override
            public <O> SerializeResult<O> serialize(SerializeContext<O> context, Operation value) {

                Map<String, O> out = new HashMap<>();
                if(value.minimum == 1 && value.maximum == Integer.MAX_VALUE) {
                    out.put("type", context.toString("any"));
                } else if(value.minimum > value.maximum) {
                    out.put("type", context.toString("all"));
                } else {
                    out.put("type", context.toString("amount"));
                    out.put("minimum", context.toNumber(value.minimum));
                    out.put("maximum", context.toNumber(value.maximum));
                }

                return SerializeResult.success(context.toMap(out));
            }

            @Override
            public <O> SerializeResult<Operation> deserialize(SerializeContext<O> context, O value) {

                String type = context.asString(context.get("type", value));
                if(type == null) {
                    return SerializeResult.failure("Value is missing");
                }

                switch (type) {
                    case "any":
                        return SerializeResult.success(Operation.ANY);
                    case "all":
                        return SerializeResult.success(Operation.ALL);
                    case "amount":
                        Number min = context.asNumber(context.get("minimum", value));
                        Number max = context.asNumber(context.get("maximum", value));
                        if(min == null || max == null) {
                            return SerializeResult.failure("Operation of type amount requires a minimum and maximum value!");
                        }
                        int iMin = min.intValue(), iMax = max.intValue();
                        if(iMin > iMax) {
                            return SerializeResult.failure("Minimum bound cannot be greater than maximum bound!");
                        }

                        return SerializeResult.success(Operation.between(iMin, iMax));
                }

                return SerializeResult.failure("Unknown type");
            }
        };

    }

}
