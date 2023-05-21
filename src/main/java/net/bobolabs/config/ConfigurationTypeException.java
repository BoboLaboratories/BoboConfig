/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Third party contributors
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

package net.bobolabs.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * An exception that is raised when a configuration contains some value that cannot be converted to the requested type.
 *
 * @since 2.0.0
 */
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
