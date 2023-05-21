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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * An optional annotation used on {@link ConfigurationDescription}'s enum constants
 * to provide information on how to load the associated {@link Configuration}
 * through a {@link ConfigurationManager} instance.
 *
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Config {


    /**
     * Specifies the {@link Configuration}'s path, relative to the {@link ConfigurationManager}'s directory.
     * <p>
     * If not specified, the path is automatically computed based on annotation {@link Enum#name() name()}.
     *
     * @return the {@link Configuration}'s path, relative to the {@link ConfigurationManager}'s directory.
     * @since 2.0.0
     */
    @NotNull String path() default ConfigDefaults.PATH;


    /**
     * Specifies the {@link Configuration}'s default resource path inside jar resources.
     * <p>
     * If not specified, {@link #path()} is used instead.
     *
     * @return the {@link Configuration}'s default resource path inside jar resources.
     * @since 2.0.0
     */
    @NotNull String defaultResource() default ConfigDefaults.RESOURCE;


    /**
     * Specifies whether the associated {@link Configuration} should be loaded with enabled auto save.
     * <p>
     * Auto save is disabled by default.
     *
     * @return whether the associated {@link Configuration} should be loaded with enabled auto save.
     * @since 2.0.0
     */
    boolean autoSave() default ConfigDefaults.AUTO_SAVE;


    /**
     * Specifies whether the associated {@link Configuration} should be saved from the default
     * resource if the file is missing when {@link ConfigurationManager#load(Enum) ConfigurationManager.load()} variants are called.
     * <p>
     * Disabled by default.
     *
     * @return whether the associated {@link Configuration} should be saved from the default resource if the file is
     * missing when {@link ConfigurationManager#load(Enum) ConfigurationManager.load()} variants are called.
     * @since 2.0.0
     */
    boolean saveDefaultResource() default ConfigDefaults.SAVE_DEFAULT_RESOURCE;

}
