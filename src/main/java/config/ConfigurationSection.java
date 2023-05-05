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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface ConfigurationSection {

    boolean contains(@NotNull String path);

    Object get(@NotNull String path);

    <T> T get(@NotNull String path, T def);

    void set(@NotNull String path, @Nullable Object value);

    ConfigurationSection getSection(@NotNull String path);

    @Contract("-> new")
    @NotNull
    Collection<String> getKeys();

    byte getByte(@NotNull String path);

    byte getByte(@NotNull String path, byte def);

    @Contract("_ -> new")
    @NotNull
    List<Byte> getByteList(@NotNull String path);

    short getShort(@NotNull String path);

    short getShort(@NotNull String path, short def);

    @Contract("_ -> new")
    @NotNull
    List<Short> getShortList(@NotNull String path);

    int getInt(@NotNull String path);

    int getInt(@NotNull String path, int def);

    @Contract("_ -> new")
    @NotNull
    List<Integer> getIntList(@NotNull String path);

    long getLong(@NotNull String path);

    long getLong(@NotNull String path, long def);

    @Contract("_ -> new")
    @NotNull
    List<Long> getLongList(@NotNull String path);

    float getFloat(@NotNull String path);

    float getFloat(@NotNull String path, float def);

    @Contract("_ -> new")
    @NotNull
    List<Float> getFloatList(@NotNull String path);

    double getDouble(@NotNull String path);

    double getDouble(@NotNull String path, double def);

    @Contract("_ -> new")
    @NotNull
    List<Double> getDoubleList(@NotNull String path);

    boolean getBoolean(@NotNull String path);

    boolean getBoolean(@NotNull String path, boolean def);

    @Contract("_ -> new")
    @NotNull
    List<Boolean> getBooleanList(@NotNull String path);

    char getChar(@NotNull String path);

    char getChar(@NotNull String path, char def);

    @Contract("_ -> new")
    @NotNull
    List<Character> getCharList(@NotNull String path);

    String getString(@NotNull String path);

    String getString(@NotNull String path, String def);

    @Contract("_ -> new")
    @NotNull
    List<String> getStringList(@NotNull String path);

    @NotNull
    List<?> getList(@NotNull String path);

    @NotNull
    List<?> getList(@NotNull String path, List<?> def);


    // Extra utils not included in the native md_5 (bungee) configuration library

    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass);

    // TODO <T> T getObject(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

    // TODO <T> T getObjectList(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

}
