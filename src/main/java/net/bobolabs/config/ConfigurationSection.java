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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface ConfigurationSection {

    boolean contains(@NotNull String path);

    @Nullable
    Object get(@NotNull String path);

    @Nullable
    @Contract("_, !null -> !null")
    <T> T get(@NotNull String path, @Nullable T def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Object> getList(@NotNull String path);

    void set(@NotNull String path, @Nullable Object value);

    @Nullable
    ConfigurationSection getSection(@NotNull String path);

    @Nullable
    @Contract("_, !null -> !null")
    ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def);

    @NotNull
    @Contract("_ -> new")
    Collection<@NotNull String> getKeys(boolean deep);

    byte getByte(@NotNull String path);

    byte getByte(@NotNull String path, byte def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Byte> getByteList(@NotNull String path);

    short getShort(@NotNull String path);

    short getShort(@NotNull String path, short def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Short> getShortList(@NotNull String path);

    int getInt(@NotNull String path);

    int getInt(@NotNull String path, int def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Integer> getIntList(@NotNull String path);

    long getLong(@NotNull String path);

    long getLong(@NotNull String path, long def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Long> getLongList(@NotNull String path);

    float getFloat(@NotNull String path);

    float getFloat(@NotNull String path, float def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Float> getFloatList(@NotNull String path);

    double getDouble(@NotNull String path);

    double getDouble(@NotNull String path, double def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Double> getDoubleList(@NotNull String path);

    boolean getBoolean(@NotNull String path);

    boolean getBoolean(@NotNull String path, boolean def);

    @NotNull
    @Contract("_ -> new")
    List<@Nullable Boolean> getBooleanList(@NotNull String path);

    @Nullable
    String getString(@NotNull String path);

    @Nullable
    @Contract("_, !null -> !null")
    String getString(@NotNull String path, @Nullable String def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull String> getStringList(@NotNull String path);

    @Nullable <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass);

    @Nullable
    @Contract("_, _, !null -> !null")
    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass, @Nullable T def);

    @NotNull
    @Contract("_, _ -> new")
    <T extends Enum<T>> List<@NotNull T> getEnumList(@NotNull String path, @NotNull Class<T> enumClass);


    // TODO <T> T getObject(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

    // TODO <T> T getObjectList(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

}
