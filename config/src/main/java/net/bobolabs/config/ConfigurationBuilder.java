/*
 * This file is part of BoboLibs.
 *
 * Copyright (C) 2023 BoboLabs.net
 *
 * BoboLibs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoboLibs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoboLibs. If not, see <http://www.gnu.org/licenses/>.
 */

package net.bobolabs.config;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ConfigurationBuilder {

    private final File file;

    private boolean autoSave = false;
    private String defaultResource = null;

    private ConfigurationBuilder(@NotNull File file) {
        this.file = file;
    }

    public static @NotNull ConfigurationBuilder fromFile(@NotNull File file) {
        return new ConfigurationBuilder(file);
    }

    public static @NotNull ConfigurationBuilder fromFile(@NotNull File folder, @NotNull String file) {
        return fromFile(new File(folder, file));
    }

    public @NotNull ConfigurationBuilder autoSave(boolean autoSave) {
        this.autoSave = autoSave;
        return this;
    }

    public @NotNull ConfigurationBuilder saveDefaultFromResource(@NotNull String defaultResource) {
        this.defaultResource = defaultResource;
        return this;
    }

    public @NotNull Configuration build() {
        return new Configuration(file, defaultResource, autoSave);
    }

}
