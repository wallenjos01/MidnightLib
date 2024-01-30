package org.wallentines.midnightlib.requirement;

import org.wallentines.mdcfg.ConfigPrimitive;
import org.wallentines.mdcfg.Functions;
import org.wallentines.mdcfg.serializer.InlineSerializer;
import org.wallentines.mdcfg.serializer.SerializeContext;
import org.wallentines.mdcfg.serializer.SerializeResult;

import java.util.Objects;
import java.util.function.Function;

public class NumberRequirement<T> extends Requirement<T> {

    public static <T> RequirementType<T> type(Function<T, Number> getter) {
        return type(getter, NumberRequirement::new);
    }

    public static <T> RequirementType<T> type(Function<T, Number> getter, Functions.F4<RequirementType<T>, Function<T, Number>, Operation, Number, Requirement<T>> builder) {
        return new RequirementType<>() {
            @Override
            public <C> SerializeResult<Requirement<T>> create(SerializeContext<C> ctx, C value) {

                return SerializeResult.ofNullable(ctx.asNumber(value)).flatMap(n -> builder.apply(this, getter, Operation.EQUAL, n)).mapError(() ->
                    SerializeResult.ofNullable(ctx.asString(value), "Expected a String or Number!").map(str -> {

                        int firstDigit = -1;
                        for(int i = 0 ; i < str.length() ; i++) {
                            char c = str.charAt(i);
                            if(c >= '0' && c <= '9' || c == '.') {
                                firstDigit = i;
                            }
                        }
                        if(firstDigit == -1) return SerializeResult.failure("No number could be parsed!");

                        Number num;
                        try {
                            String numStr = str.substring(firstDigit);
                            if(numStr.contains(".")) {
                                num = Double.parseDouble(numStr);
                            } else {
                                num = Long.parseLong(numStr);
                            }
                        } catch (NumberFormatException ex) {
                            return SerializeResult.failure("An error occurred while parsing a number! " + ex.getMessage());
                        }

                        Operation op = Operation.EQUAL;
                        if(firstDigit > 0) {
                            op = Operation.SERIALIZER.readString(str.substring(0, firstDigit));
                        }
                        return SerializeResult.success(builder.apply(this, getter, op, num));
                    })
                );
            }
        };
    }

    private final Function<T, Number> getter;
    private final Operation operation;
    private final Number value;

    public NumberRequirement(RequirementType<T> type, Function<T, Number> getter, Operation op, Number value) {
        super(type);
        this.getter = getter;
        this.operation = op;
        this.value = value;
    }

    @Override
    public boolean check(T data) {

        Number n = getter.apply(data);
        boolean integer = ConfigPrimitive.isInteger(value) && ConfigPrimitive.isInteger(n);
        switch (operation) {
            case GREATER:
                return integer ? n.longValue() > value.longValue() : n.doubleValue() > value.doubleValue();
            case LESS:
                return integer ? n.longValue() < value.longValue() : n.doubleValue() < value.doubleValue();
            case GREATER_EQUAL:
                return integer ? n.longValue() >= value.longValue() : n.doubleValue() >= value.doubleValue();
            case LESS_EQUAL:
                return integer ? n.longValue() <= value.longValue() : n.doubleValue() <= value.doubleValue();
            case EQUAL:
                return n.equals(value);
        }

        return false;
    }

    @Override
    public <C> SerializeResult<C> serialize(SerializeContext<C> ctx) {
        return SerializeResult.success(ctx.toNumber(value));
    }

    public enum Operation {
        GREATER(">"),
        LESS("<"),
        GREATER_EQUAL(">="),
        LESS_EQUAL("<="),
        EQUAL("=");

        private final String value;

        Operation(String value) {
            this.value = value;
        }

        public static final InlineSerializer<Operation> SERIALIZER = InlineSerializer.of(op -> op.value, value -> {
            for(Operation o : values()) {
                if(o.value.equals(value)) {
                    return o;
                }
            }
            return null;
        });
    }
}
