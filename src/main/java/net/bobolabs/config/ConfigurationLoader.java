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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


/**
 * An object used to load a {@link Configuration} object from
 * file according to options provided through its method calls.
 *
 * @since 2.0.0
 */
public final class ConfigurationLoader {

    private final File file;

    private boolean autoSave = false;
    private boolean saveDefaultResource = false;
    private String defaultResource = null;

    private ConfigurationLoader(@NotNull File file) {
        this.file = file;
    }


    /**
     * Provides information on which file is to be loaded.
     *
     * @param file the file that is to be loaded.
     * @return a new configuration loader.
     * @since 2.0.0
     */
    public static @NotNull ConfigurationLoader fromFile(@NotNull File file) {
        // Do not accept directories
        if (file.isDirectory()) {
            throw new IllegalArgumentException(file + " is a directory");
        }
        return new ConfigurationLoader(file);
    }


    /**
     * Provides information on which file is to be loaded.
     *
     * @param file the string representation of the path to the file that is to be loaded.
     * @return a new configuration loader.
     * @since 2.0.0
     */
    public static @NotNull ConfigurationLoader fromFile(@NotNull String file) {
        return fromFile(new File(file));
    }


    /**
     * Provides information on which file is to be loaded.
     *
     * @param directory the directory inside which the file that is to be loaded resides.
     * @param file      the string representation of the path to the file that
     *                  is to be loaded, relative to the provided directory.
     * @return a new configuration loader.
     * @since 2.0.0
     */
    public static @NotNull ConfigurationLoader fromFile(@NotNull File directory, @NotNull String file) {
        return fromFile(new File(directory, file));
    }


    /**
     * Sets whether changes made to the {@link Configuration} should be
     * automatically saved to file as soon as they are made.
     * <p>
     * Note that enabling auto save could become very inefficient in case of
     * frequent write operations due to the blocking nature of I/O; that is
     * why auto save is disable by default and requires manual enable.
     *
     * @param autoSave {@code true} to enable auto save,
     *                 {@code false} to disable it (default behaviour).
     * @return the current configuration loader itself.
     * @since 2.0.0
     */
    public @NotNull ConfigurationLoader autoSave(boolean autoSave) {
        this.autoSave = autoSave;
        return this;
    }


    /**
     * Sets the path to the resource file which contains the default configuration.
     * <p>
     * Such file must be bundled within the resulting jar to allow for automatic
     * configuration file creation when calling {@link #load()}.<br>
     * The default resource is saved if and only if the file specified with
     * any of the {@link #fromFile} method variants could not be found.<br><br>
     *
     * @param defaultResource the path to the resource file which contains the default configuration.
     * @return the current configuration loader itself.
     * @since 2.0.0
     */
    public @NotNull ConfigurationLoader setDefaultResource(@NotNull String defaultResource) {
        this.saveDefaultResource = true;
        this.defaultResource = defaultResource;
        return this;
    }


    /**
     * Loads the {@link Configuration} as specified within this configuration loader.
     *
     * @return the loaded configuration.
     * @since 2.0.0
     */
    public @NotNull Configuration load() {
        if (!file.exists()) {
            // Fail if file doesn't exist and save default was not requested
            if (!saveDefaultResource) {
                throw new IllegalStateException("file should not be saved from resource but does not exist");
            }

            // Create folder structure
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs(); // TODO do not ignore return value
            }

            // TODO uhm
            if (defaultResource == null) {
                defaultResource = file.getName();
            }

            // Copy file from resources
            if (!defaultResource.startsWith("/")) {
                defaultResource = "/".concat(defaultResource);
            }
            try (InputStream in = getClass().getResourceAsStream(defaultResource)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Configuration(file, autoSave);
    }

}
