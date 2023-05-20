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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConfigurationTypeException extends ConfigurationException {

    ConfigurationTypeException(@NotNull String path, @NotNull Class<?> requestedType, @Nullable Object value) {
        super(prepareMessage(path, requestedType, value));
    }

    private static @NotNull String prepareMessage(@NotNull String path,
                                                  @NotNull Class<?> requestedType,
                                                  @Nullable Object value) {
        StringBuilder builder = new StringBuilder();

        if (value == null) {
            builder.append("value `null`");
        } else if (value instanceof ConfigurationSection) {
            builder.append(ConfigurationSection.class.getPackageName())
                    .append(".")
                    .append(ConfigurationSection.class.getSimpleName());
        } else {
            builder.append("value `")
                    .append(value)
                    .append("` of type ")
                    .append(value.getClass());
        }

        builder.append(" found in path `")
                .append(path)
                .append("` cannot be converted to ")
                .append(requestedType);

        return builder.toString();
    }

}
