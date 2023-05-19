/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Third party contributors
 *
 * BoboConfig is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoboConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoboConfig.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bobolabs.config;

import java.util.function.Function;
import java.util.function.UnaryOperator;

final class TypeConverters {

    private TypeConverters() {
    }

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
