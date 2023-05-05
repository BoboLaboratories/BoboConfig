/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
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

package config;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ConfigurationBuilder {

    private final File file;

    private boolean autoSave = false;
    private boolean saveDefaultResource = false;
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

    public @NotNull ConfigurationBuilder saveDefaultResource(boolean saveDefaultResource) {
        this.saveDefaultResource = saveDefaultResource;
        return this;
    }

    public @NotNull ConfigurationBuilder setDefaultResource(@NotNull String defaultResource) {
        this.saveDefaultResource = true;
        this.defaultResource = defaultResource;
        return this;
    }

    public @NotNull Configuration build() {
        return new Configuration(file, defaultResource, saveDefaultResource, autoSave);
    }

}
