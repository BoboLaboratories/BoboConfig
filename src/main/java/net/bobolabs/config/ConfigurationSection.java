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

import java.util.List;
import java.util.Set;

public interface ConfigurationSection {

    /**
     * Returns {@code true} if this configuration section
     * contains a mapping for the specified {@code path}.
     *
     * @param path the path whose presence in this configuration
     *             section is to be tested.
     * @return {@code true} if this configuration section
     *         contains a mapping for the specified {@code path}.
     * @since 2.0.0
     */
    boolean contains(@NotNull String path);


    /**
     * Returns the object mapped to the specified {@code path},
     * relative to this configuration section, or {@code null}
     * if no mapping is present.
     *
     * @param path the path whose associated value is to be returned.
     * @return the object mapped to the specified {@code path},
     *         relative to this configuration section,
     *         or {@code null} if no mapping is present.
     * @since 2.0.0
     */
    @Nullable
    Object get(@NotNull String path);


    /**
     * Returns the value mapped to the specified {@code path},
     * relative to this configuration section, or the specified
     * default value if no mapping is present.
     *
     * @param path the path whose associated value is to be returned.
     * @param def the default object to be returned if no mapping is
     *            present for the specified {@code path}.
     * @return the value mapped to the specified {@code path},
     *         relative to this configuration section, or the
     *         specified default value if no mapping is present.
     * @since 2.0.0
     */
    @Nullable
    @Contract("_, !null -> !null")
    <T> T get(@NotNull String path, @Nullable T def);


    /**
     * Returns the value mapped to the specified {@code path},
     * relative to this configuration section, if present,
     * otherwise a mapping from {@code path} to {@code value}
     * is established and {@code value} is returned.
     *
     * @param path the path whose associated value is to be returned (and possibly set).
     * @param value the value to be mapped to {@code path} if no mapping is present.
     * @return the value mapped to the specified {@code path},
     *         relative to this configuration section, or
     *         {@code value} if no mapping is present.
     * @since 2.0.0
     */
    @NotNull
    <T> T getOrSet(@NotNull String path, @NotNull T value);


    /**
     * Returns a new list which contains all the values
     * mapped to {@code path}, including {@code null} values,
     * or a new empty list if no mapping is present.<br><br>
     *
     * @param path the path whose associated values are to be returned.
     * @return a new list which contains all the values mapped to
     *         {@code path}, including {@code null} values, or a
     *         new empty list if no mapping is present.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@Nullable Object> getList(@NotNull String path);


    /**
     * Establishes a new mapping from the specified {@code path} to
     * the given {@code value}, overriding any pre-existing mapping.
     * Passing {@code null} as {@code value} will have the same effect
     * as calling {@link #unset(String) unset} on the same {@code path}.
     *
     * @param path the path to be mapped to the given value.
     * @param value the value to be mapped to the specified path.
     * @since 2.0.0
     */
    void set(@NotNull String path, @Nullable Object value);


    /**
     * Delete any existing mapping for {@code path}, which means
     * {@code path} is completely removed from this section.
     *
     * @param path the path to be removed.
     * @since 2.0.0
     */
    void unset(@NotNull String path);


    /**
     * Creates a new configuration section under the specified
     * {@code path} and returns it.
     *
     * @param path the path under which the new configuration
     *             section is to be created.
     * @return the newly created configuration section.
     * @since 2.0.0
     */
    @NotNull
    ConfigurationSection createSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * if present, otherwise a new configuration section is created,
     * mapped to {@code path} and returned.
     *
     * @param path the path whose associated configuration section is to be
     *             returned (and created if not already present).
     * @return the pre-existing or the newly created configuration section.
     * @since 2.0.0
     */
    @NotNull
    ConfigurationSection getOrCreateSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * or throws {@link NullPointerException} if not present.
     *
     * @param path the path whose associated configuration section is to be returned.
     * @return the configuration section associated to the specified path.
     * @throws NullPointerException if no mapping is found for the given path.
     * TODO throw something else if a mapping is found but is not a section.
     * @since 2.0.0
     */
    @NotNull
    ConfigurationSection getSection(@NotNull String path);

    @Nullable
    @Contract("_, !null -> !null")
    ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def);

    @Nullable
    ConfigurationSection getOptionalSection(@NotNull String path);

    @NotNull
    @Contract("_ -> new")
    Set<@NotNull String> getKeys(@NotNull TraversalMode traversalMode);

    byte getByte(@NotNull String path);

    byte getByte(@NotNull String path, byte def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Byte> getByteList(@NotNull String path);

    short getShort(@NotNull String path);

    short getShort(@NotNull String path, short def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Short> getShortList(@NotNull String path);

    int getInt(@NotNull String path);

    int getInt(@NotNull String path, int def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Integer> getIntList(@NotNull String path);

    long getLong(@NotNull String path);

    long getLong(@NotNull String path, long def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Long> getLongList(@NotNull String path);

    float getFloat(@NotNull String path);

    float getFloat(@NotNull String path, float def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Float> getFloatList(@NotNull String path);

    double getDouble(@NotNull String path);

    double getDouble(@NotNull String path, double def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Double> getDoubleList(@NotNull String path);

    boolean getBoolean(@NotNull String path);

    boolean getBoolean(@NotNull String path, boolean def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull Boolean> getBooleanList(@NotNull String path);

    @NotNull
    String getString(@NotNull String path);

    @Nullable
    @Contract("_, !null -> !null")
    String getString(@NotNull String path, @Nullable String def);

    @NotNull
    @Contract("_ -> new")
    List<@NotNull String> getStringList(@NotNull String path);

    @NotNull <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass);

    @Nullable
    @Contract("_, _, !null -> !null")
    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass, @Nullable T def);

    @NotNull
    @Contract("_, _ -> new")
    <T extends Enum<T>> List<@NotNull T> getEnumList(@NotNull String path, @NotNull Class<T> enumClass);


    // TODO: <T> T getObject(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

    // TODO: <T> T getObjectList(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

}
