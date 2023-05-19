package net.bobolabs.config;

import javax.print.DocFlavor;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

final class TypeConverters {

    private TypeConverters() {}

    static final Function<Integer, Byte> BYTE = val -> {
        if (val < Byte.MIN_VALUE || val > Byte.MAX_VALUE) {
            throw new ClassCastException(val.getClass() + " could not be cast to " + Byte.class);
        }
        return val.byteValue();
    };

    static final Function<Integer, Short> SHORT = val -> {
        if (val < Short.MIN_VALUE || val > Short.MAX_VALUE) {
            throw new ClassCastException(val.getClass() + " could not be cast to " + Short.class);
        }
        return val.shortValue();
    };

    static final UnaryOperator<Integer> INTEGER = val -> val;

    static final Function<Object, Long> LONG = val -> {
        if (val instanceof Integer integer) {
            return integer.longValue();
        } else if (val instanceof Long longVal) {
            return longVal;
        }
        throw new ClassCastException(val.getClass() + " could not be cast to " + Short.class);
    };

    static final Function<Number, Float> FLOAT = val -> {
        if (val instanceof Integer integer) {
            return integer.floatValue();
        } else if (val instanceof Double doubleVal) {
            float floatVal = doubleVal.floatValue();
            if (floatVal >= -Float.MAX_VALUE && floatVal <= Float.MAX_VALUE) {
                return floatVal;
            }
        }
        throw new ClassCastException(val.getClass() + " could not be cast to " + Float.class);
    };

    static final Function<Number, Double> DOUBLE = val -> {
        if (val instanceof Integer integer) {
            return integer.doubleValue();
        } else if (val instanceof Long longVal) {
            return longVal.doubleValue();
        } else if (val instanceof Double doubleVal) {
            return doubleVal;
        }
        throw new ClassCastException(val.getClass() + " could not be cast to " + Float.class);
    };

    static final Function<Boolean, Boolean> BOOLEAN = val -> val;

}
