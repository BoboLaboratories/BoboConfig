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
     * @since  2.0.0
     */
    String path() default ConfigDefaults.PATH;


    /**
     * Specifies the {@link Configuration}'s default resource path inside jar resources.
     * <p>
     * If not specified, {@link #path()} is used instead.
     *
     * @return the {@link Configuration}'s default resource path inside jar resources.
     * @since  2.0.0
     */
    String defaultResource() default ConfigDefaults.RESOURCE;


    /**
     * Specifies whether the associated {@link Configuration} should be loaded with enabled auto save.
     * <p>
     * Auto save is disabled by default.
     *
     * @return whether the associated {@link Configuration} should be loaded with enabled auto save.
     * @since  2.0.0
     */
    boolean autoSave() default ConfigDefaults.AUTO_SAVE;


    /**
     * Specifies whether the associated {@link Configuration} should be saved from the default
     * resource if the file is missing when {@link ConfigurationManager#load(Enum) ConfigurationManager.load()} variants are called.
     * <p>
     * Disabled by default.
     *
     * @return whether the associated {@link Configuration} should be saved from the default resource if the file is
     *         missing when {@link ConfigurationManager#load(Enum) ConfigurationManager.load()} variants are called.
     * @since  2.0.0
     */
    boolean saveDefaultResource() default ConfigDefaults.SAVE_DEFAULT_RESOURCE;

}
