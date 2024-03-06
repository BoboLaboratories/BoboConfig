/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023-2024 BoboLabs.net
 * Copyright (C) 2023-2024 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023-2024 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023-2024 Third party contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net. bobolabs.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * An exception that is raised when a list inside a configuration contains
 * some element that cannot be converted to the requested list type.
 *
 * @since 2.0.0
 */
public final class ConfigurationListTypeException extends ConfigurationException {

    ConfigurationListTypeException(@NotNull String path,
                                   @NotNull Class<?> requestedType,
                                   @NotNull List<?> list,
                                   @Nullable Object value) {
        super(prepareMessage(path, requestedType, list, value));
    }

    private static @NotNull String prepareMessage(@NotNull String path,
                                                  @NotNull Class<?> requestedType,
                                                  @NotNull List<?> list,
                                                  @Nullable Object value) {
        StringBuilder builder = new StringBuilder();

        builder.append("list `")
                .append(list)
                .append("` found in path `")
                .append(path)
                .append("` could not be converted to ")
                .append(List.class)
                .append("<")
                .append(requestedType)
                .append("> because it contains ");

        if (value == null) {
            builder.append("null element(s)");
        } else {
            builder.append("value `")
                    .append(value)
                    .append("` of type ")
                    .append(value.getClass())
                    .append(" that cannot be converted to ")
                    .append(requestedType);
        }

        return builder.toString();
    }

}
