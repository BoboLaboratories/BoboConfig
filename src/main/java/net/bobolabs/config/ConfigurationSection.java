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
     * Returns {@code true} if this configuration section contains a mapping for the specified {@code path}.
     *
     * @param path  the path whose presence in this configuration section is to be tested.
     * @return      {@code true} if this configuration section contains a mapping for the specified {@code path}.
     * @since       2.0.0
     */
    boolean contains(@NotNull String path);


    /**
     * Returns the object mapped to the specified {@code path}.
     *
     * @param path  the path whose associated value is to be returned.
     * @return      the object mapped to the specified {@code path}.
     * @throws      NullPointerException if no mapping is present for the given path.
     * @since       2.0.0
     */
    @NotNull
    Object get(@NotNull String path);


    /**
     * Returns the value mapped to the specified {@code path},
     * relative to this configuration section, or the specified
     * default value if no mapping is present.
     *
     * @param path  the path whose associated value is to be returned.
     * @param def   the default object to be returned if no mapping is
     *              present for the specified {@code path}.
     * @return      the value mapped to the specified {@code path},
     *              relative to this configuration section, or the
     *              specified default value if no mapping is present.
     * @since       2.0.0
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
     * @param path  the path whose associated value is to be returned (and possibly set).
     * @param value the value to be mapped to {@code path} if no mapping is present.
     * @return      the value mapped to the specified {@code path},
     *              relative to this configuration section, or
     *              {@code value} if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    <T> T getOrSet(@NotNull String path, @NotNull T value);


    /**
     * Returns a new list which contains all the values
     * mapped to {@code path}, including {@code null} values,
     * or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated values are to be returned.
     * @return      a new list which contains all the values mapped to
     *              {@code path}, including {@code null} values, or a
     *              new empty list if no mapping is present.
     * @since       2.0.0
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
     * @param path  the path to be mapped to the given value.
     * @param value the value to be mapped to the specified path.
     * @since       2.0.0
     */
    void set(@NotNull String path, @Nullable Object value);


    /**
     * Delete any existing mapping for {@code path}, which means
     * {@code path} is completely removed from this section.
     *
     * @param path  the path to be removed.
     * @since       2.0.0
     */
    void unset(@NotNull String path);


    /**
     * Creates a new configuration section under the specified
     * {@code path} and returns it.
     *
     * @param path  the path under which the new configuration
     *              section is to be created.
     * @return      the newly created configuration section.
     * @throws      IllegalArgumentException if the given path is already associated with
     *                                       anything but a configuration section.
     * @since       2.0.0
     */
    @NotNull
    ConfigurationSection createSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * if present, otherwise a new configuration section is created,
     * mapped to {@code path} and returned.
     *
     * @param path  the path whose associated configuration section is to be
     *              returned (and created if not already present).
     * @return      the pre-existing or the newly created configuration section.
     * @throws      ClassCastException  if the given path is already associated with
     *                                  anything but a configuration section.
     * @since       2.0.0
     */
    @NotNull
    ConfigurationSection getOrCreateSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * or throws {@link NullPointerException} if not present.
     *
     * @param path  the path whose associated configuration section is to be returned.
     * @return      the configuration section associated to the specified path.
     * @throws      NullPointerException if no mapping is present for the given path.
     * @throws      ClassCastException   if the value mapped to the given path is
     *                                   not a configuration section.
     * @since       2.0.0
     */
    @NotNull
    ConfigurationSection getSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * or the specified default one if no mapping is present.
     *
     * @param path  the path whose associated configuration section is to be returned.
     * @param def   the default configuration section to be returned if no mapping is
     *              present for the specified {@code path}.
     * @return      the configuration section mapped to the specified {@code path},
     *              or the specified default one if no mapping is present.
     * @throws      ClassCastException if the value mapped to the given path is
     *                                 not a configuration section.
     * @since       2.0.0
     */
    @Nullable
    @Contract("_, !null -> !null")
    ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * or {@code null} if not present.
     *
     * @param path  the path whose associated configuration section is to be returned.
     * @return      the configuration section associated to the specified path,
     *              or {@code null} if not present.
     * @throws      ClassCastException if the value mapped to the given path is
     *                                 not a configuration section.
     * @since       2.0.0
     */
    @Nullable
    ConfigurationSection getOptionalSection(@NotNull String path);


    /**
     * Returns a new set which contains all the keys of this configuration
     * section according to se specified {@link TraversalMode}, or a new
     * empty set if this configuration section is empty.<br><br>
     *
     * For more information about traversal modes, plase refer to
     * {@link TraversalMode} javadocs.
     *
     * @param traversalMode the traversal mode used for generating the keys
     *                      that are to be returned.
     * @return              a new set which contains all the keys of this configuration
     *                      section according to se specified {@link TraversalMode}.
     * @since               2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    Set<@NotNull String> getKeys(@NotNull TraversalMode traversalMode);


    /**
     * Returns the byte value associated to the specified path.
     *
     * @param path  the path whose associated value is to be returned.
     * @return      the byte value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      ClassCastException   if the value associated to the given path
     *                                   could not be converted to byte.
     * @since       2.0.0
     */
    byte getByte(@NotNull String path);


    /**
     * Returns the byte value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated byte value is to be returned.
     * @return      the byte value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      ClassCastException if the value associated to the given path
     *                                 could not be converted to byte.
     * @since       2.0.0
     */
    byte getByte(@NotNull String path, byte def);


    /**
     * Returns a new list which contains all the byte values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated byte values are to be returned.
     * @return      a new list which contains all the byte values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * TODO same throws as getByte(String)
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Byte> getByteList(@NotNull String path);


    /**
     * Returns the short value associated to the specified path.
     *
     * @param path  the path whose associated short value is to be returned.
     * @return      the short value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    short getShort(@NotNull String path);


    /**
     * Returns the short value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated short value is to be returned.
     * @return      the short value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    short getShort(@NotNull String path, short def);


    /**
     * Returns a new list which contains all the short values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated short values are to be returned.
     * @return      a new list which contains all the short values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Short> getShortList(@NotNull String path);


    /**
     * Returns the integer value associated to the specified path.
     *
     * @param path  the path whose associated integer value is to be returned.
     * @return      the integer value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    int getInt(@NotNull String path);


    /**
     * Returns the integer value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated integer value is to be returned.
     * @return      the integer value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    int getInt(@NotNull String path, int def);


    /**
     * Returns a new list which contains all the integer values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated integer values are to be returned.
     * @return      a new list which contains all the integer values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Integer> getIntList(@NotNull String path);


    /**
     * Returns the long value associated to the specified path.
     *
     * @param path  the path whose associated long value is to be returned.
     * @return      the long value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    long getLong(@NotNull String path);


    /**
     * Returns the long value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated long value is to be returned.
     * @return      the long value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    long getLong(@NotNull String path, long def);


    /**
     * Returns a new list which contains all the long values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated long values are to be returned.
     * @return      a new list which contains all the long values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Long> getLongList(@NotNull String path);


    /**
     * Returns the float value associated to the specified path.
     *
     * @param path  the path whose associated float value is to be returned.
     * @return      the float value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    float getFloat(@NotNull String path);


    /**
     * Returns the float value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated float value is to be returned.
     * @return      the float value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    float getFloat(@NotNull String path, float def);


    /**
     * Returns a new list which contains all the float values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated float values are to be returned.
     * @return      a new list which contains all the float values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Float> getFloatList(@NotNull String path);


    /**
     * Returns the double value associated to the specified path.
     *
     * @param path  the path whose associated double value is to be returned.
     * @return      the double value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    double getDouble(@NotNull String path);


    /**
     * Returns the double value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated double value is to be returned.
     * @return      the double value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    double getDouble(@NotNull String path, double def);


    /**
     * Returns a new list which contains all the double values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated double values are to be returned.
     * @return      a new list which contains all the double values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Double> getDoubleList(@NotNull String path);


    /**
     * Returns the boolean value associated to the specified path.
     *
     * @param path  the path whose associated boolean value is to be returned.
     * @return      the boolean value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    boolean getBoolean(@NotNull String path);


    /**
     * Returns the boolean value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated boolean value is to be returned.
     * @return      the boolean value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      TODO exception if it is not of the correct type
     * @since       2.0.0
     */
    boolean getBoolean(@NotNull String path, boolean def);


    /**
     * Returns a new list which contains all the boolean values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated boolean values are to be returned.
     * @return      a new list which contains all the boolean values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Boolean> getBooleanList(@NotNull String path);


    /**
     * Returns the string representation of the value associated to the specified path.
     *
     * @param path  the path whose associated string value is to be returned.
     * @return      the string value associated to the specified path.
     * @throws      NullPointerException if no mapping is found for the given path.
     * @throws      ClassCastException   if the value mapped to the given path has
     *                                   no string representation.
     * @since       2.0.0
     */
    @NotNull
    String getString(@NotNull String path);


    /**
     * Returns the string value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated string value is to be returned.
     * @return      the string value associated to the specified path, or the
     *              specified default value if no mapping is present.
     * @throws      ClassCastException if the value mapped to the given path has
     *                                 no string representation.
     * @since       2.0.0
     */
    @Nullable
    @Contract("_, !null -> !null")
    String getString(@NotNull String path, @Nullable String def);


    /**
     * Returns a new list which contains all the string values mapped to
     * {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated string values are to be returned.
     * @return      a new list which contains all the string values mapped to
     *              {@code path}, or a new empty list if no mapping is present.
     * @throws      NullPointerException if any element int the list is {@code null}.
     * @throws      ClassCastException   if any element in the list has no string representation.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull String> getStringList(@NotNull String path);


    /**
     * Returns the enum value in {@code enumClass} associated to the specified path.
     *
     * @param path  the path whose associated enum value is to be returned.
     * @return      the enum value in {@code enumClass} associated to the specified path.
     * @throws      NullPointerException     if no mapping is found for the given path.
     * @throws      IllegalArgumentException if the value is not part of {@code enumClass}.
     * @since       2.0.0
     */
    @NotNull <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass);


    /**
     * Returns the enum value in {@code enumClass} associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path  the path whose associated enum value in {@code enumClass} is to be returned.
     * @return      the enum value in {@code enumClass} associated to the specified path,
     *              or the specified default value if no mapping is present.
     *
     * @since       2.0.0
     */
    @Nullable
    @Contract("_, _, !null -> !null")
    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass, @Nullable T def);


    /**
     * Returns a new list which contains all the enum values in {@code enumClass}
     * mapped to {@code path}, or a new empty list if no mapping is present.
     *
     * @param path  the path whose associated enum values in {@code enumClass} values are to be returned.
     * @return      a new list which contains all the enum values in {@code enumClass} mapped
     *              to {@code path}, or a new empty list if no mapping is present.
     * @since       2.0.0
     */
    @NotNull
    @Contract("_, _ -> new")
    <T extends Enum<T>> List<@NotNull T> getEnumList(@NotNull String path, @NotNull Class<T> enumClass);


    // TODO: <T> T getObject(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

    // TODO: <T> T getObjectList(@NotNull String path, @NotNull Function<ConfigurationSection, T> parser);

}
