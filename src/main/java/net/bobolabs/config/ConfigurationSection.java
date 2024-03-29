/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023-2024 BoboLabs.net
 * Copyright (C) 2023-2024 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023-2024 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023-2024 Third party contributors
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;


/**
 * A configuration section is anything inside a configuration that is not a primitive or list value
 * (i.e.&nbsp;any path that does not lead to a primitive or list value leads to a configuration section).<br>
 * <p>
 * A {@link Configuration} is a configuration section as well as it may contain multiple subsections
 * and can never contain just a single primitive or list value.<br>
 * <p>
 * Any operation performed on a configuration section is thread-safe.
 *
 * @since 2.0.0
 */
public interface ConfigurationSection {


    /**
     * Returns {@code true} if this configuration section contains a mapping
     * for the specified {@code path}, {@code false} otherwise.
     *
     * @param path the path whose presence in this configuration section is to be tested.
     * @return {@code true} if this configuration section contains a mapping
     * for the specified {@code path}, {@code false} otherwise.
     * @since 2.0.0
     */
    boolean contains(@NotNull String path);


    /**
     * Returns the value mapped to the specified {@code path}.
     *
     * @param path the path whose associated value is to be returned.
     * @return the value mapped to the specified {@code path}.
     * @throws NullPointerException if no mapping is present for the specified path.
     * @since 2.0.0
     */
    @NotNull
    Object get(@NotNull String path);


    /**
     * Returns the value mapped to the specified {@code path},
     * or the given default value if no mapping is present.
     *
     * @param <T>  the type whose associated value is to be cast to before returning.
     * @param path the path whose associated value is to be returned.
     * @param def  the default value to be returned if no mapping is
     *             present for the specified {@code path}.
     * @return the value mapped to the specified {@code path},
     * or the given default value if no mapping is present.
     * @since 2.0.0
     */
    @Nullable
    @Contract("_, !null -> !null")
    <T> T get(@NotNull String path, @Nullable T def);


    /**
     * Returns a new list which contains all the values mapped
     * to {@code path}, including {@code null} values.
     *
     * @param path the path whose associated values are to be returned.
     * @return a new list which contains all the values mapped to
     * {@code path}, including {@code null} values.
     * @throws NullPointerException if no mapping is present for the specified path.
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
     * @param path  the path to be mapped to the given value.
     * @param value the value to be mapped to the specified path.
     * @since 2.0.0
     */
    void set(@NotNull String path, @Nullable Object value);


    /**
     * Delete any existing mapping for {@code path}, which means
     * {@code path} is completely removed from this section.
     *
     * @param path the path whose mapping is to be removed.
     * @since 2.0.0
     */
    void unset(@NotNull String path);


    /**
     * Creates a new configuration section mapped to the specified {@code path} and returns it.
     *
     * @param path the path to which the new configuration section is to be mapped.
     * @return the newly created configuration section.
     * @throws IllegalArgumentException if there's already any mapping for the specified {@code path}.
     * @since 2.0.0
     */
    @NotNull
    ConfigurationSection createSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * if present; otherwise a new configuration section is created,
     * mapped to {@code path} and returned.
     *
     * @param path the path whose associated configuration section is to be returned,
     *             or to which the new configuration section is to be mapped.
     * @return the pre-existing or the newly created configuration section.
     * @throws ConfigurationTypeException if the specified path is already associated
     *                                    with anything but a configuration section.
     * @since 2.0.0
     */
    @NotNull
    ConfigurationSection getOrCreateSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path}.
     *
     * @param path the path whose associated configuration section is to be returned.
     * @return the configuration section associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the specified path is already associated
     *                                    with anything but a configuration section.
     * @since 2.0.0
     */
    @NotNull
    ConfigurationSection getSection(@NotNull String path);


    /**
     * Returns the configuration section mapped to the specified {@code path},
     * or the given default one if no mapping is present.
     *
     * @param path the path whose associated configuration section is to be returned.
     * @param def  the default configuration section to be returned if no mapping
     *             is present for the specified {@code path}.
     * @return the configuration section mapped to the specified {@code path},
     * or the given default one if no mapping is present.
     * @throws ConfigurationTypeException if the specified path is already associated
     *                                    with anything but a configuration section.
     * @since 2.0.0
     */
    @Nullable
    @Contract("_, !null -> !null")
    ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def);


    /**
     * Returns a new set which contains all the keys of this configuration
     * section according to se specified {@link TraversalMode}, or a new
     * empty set if this configuration section is empty.<br><br>
     * <p>
     * For more information about traversal modes, please refer to {@link TraversalMode} javadocs.
     *
     * @param traversalMode the traversal mode used for generating the keys that are to be returned.
     * @return a new set which contains all the keys of this configuration
     * section according to se specified {@link TraversalMode}, or
     * a new empty set if this configuration section is empty.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    Set<@NotNull String> getKeys(@NotNull TraversalMode traversalMode);


    /**
     * Returns the byte value associated to the specified path.
     *
     * @param path the path whose associated byte value is to be returned.
     * @return the byte value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to byte.
     * @since 2.0.0
     */
    byte getByte(@NotNull String path);


    /**
     * Returns the byte value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated byte value is to be returned.
     * @param def  the default byte value to be returned if no mapping is present for the specified {@code path}.
     * @return the byte value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to byte.
     * @since 2.0.0
     */
    byte getByte(@NotNull String path, byte def);


    /**
     * Returns a new list which contains all the byte values mapped to {@code path}.
     *
     * @param path the path whose associated byte values are to be returned.
     * @return a new list which contains all the byte values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to byte, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Byte> getByteList(@NotNull String path);


    /**
     * Returns the short value associated to the specified path.
     *
     * @param path the path whose associated short value is to be returned.
     * @return the short value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to short.
     * @since 2.0.0
     */
    short getShort(@NotNull String path);


    /**
     * Returns the short value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated short value is to be returned.
     * @param def  the default short value to be returned if no mapping is present for the specified {@code path}.
     * @return the short value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to short.
     * @since 2.0.0
     */
    short getShort(@NotNull String path, short def);


    /**
     * Returns a new list which contains all the short values mapped to {@code path}.
     *
     * @param path the path whose associated short values are to be returned.
     * @return a new list which contains all the short values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to short, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Short> getShortList(@NotNull String path);


    /**
     * Returns the integer value associated to the specified path.
     *
     * @param path the path whose associated integer value is to be returned.
     * @return the integer value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to integer.
     * @since 2.0.0
     */
    int getInt(@NotNull String path);


    /**
     * Returns the integer value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated integer value is to be returned.
     * @param def  the default integer value to be returned if no mapping is present for the specified {@code path}.
     * @return the integer value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to integer.
     * @since 2.0.0
     */
    int getInt(@NotNull String path, int def);


    /**
     * Returns a new list which contains all the integer values mapped to {@code path}.
     *
     * @param path the path whose associated integer values are to be returned.
     * @return a new list which contains all the integer values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to integer, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Integer> getIntList(@NotNull String path);


    /**
     * Returns the long value associated to the specified path.
     *
     * @param path the path whose associated long value is to be returned.
     * @return the long value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to long.
     * @since 2.0.0
     */
    long getLong(@NotNull String path);


    /**
     * Returns the long value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated long value is to be returned.
     * @param def  the default long value to be returned if no mapping is present for the specified {@code path}.
     * @return the long value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to long.
     * @since 2.0.0
     */
    long getLong(@NotNull String path, long def);


    /**
     * Returns a new list which contains all the long values mapped to {@code path}.
     *
     * @param path the path whose associated long values are to be returned.
     * @return a new list which contains all the long values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to long, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Long> getLongList(@NotNull String path);


    /**
     * Returns the float value associated to the specified path.
     *
     * @param path the path whose associated float value is to be returned.
     * @return the float value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to float.
     * @since 2.0.0
     */
    float getFloat(@NotNull String path);


    /**
     * Returns the float value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated float value is to be returned.
     * @param def  the default float value to be returned if no mapping is present for the specified {@code path}.
     * @return the float value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to float.
     * @since 2.0.0
     */
    float getFloat(@NotNull String path, float def);


    /**
     * Returns a new list which contains all the float values mapped to {@code path}.
     *
     * @param path the path whose associated float values are to be returned.
     * @return a new list which contains all the float values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to float, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Float> getFloatList(@NotNull String path);


    /**
     * Returns the double value associated to the specified path.
     *
     * @param path the path whose associated double value is to be returned.
     * @return the double value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to double.
     * @since 2.0.0
     */
    double getDouble(@NotNull String path);


    /**
     * Returns the double value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated double value is to be returned.
     * @param def  the default double value to be returned if no mapping is present for the specified {@code path}.
     * @return the double value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to double.
     * @since 2.0.0
     */
    double getDouble(@NotNull String path, double def);


    /**
     * Returns a new list which contains all the double values mapped to {@code path}.
     *
     * @param path the path whose associated double values are to be returned.
     * @return a new list which contains all the double values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to double, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Double> getDoubleList(@NotNull String path);


    /**
     * Returns the boolean value associated to the specified path.
     *
     * @param path the path whose associated boolean value is to be returned.
     * @return the boolean value associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to boolean.
     * @since 2.0.0
     */
    boolean getBoolean(@NotNull String path);


    /**
     * Returns the boolean value associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param path the path whose associated boolean value is to be returned.
     * @param def  the default boolean value to be returned if no mapping is present for the specified {@code path}.
     * @return the boolean value associated to the specified path,
     * or the given default value if no mapping is present.
     * @throws ConfigurationTypeException if the value associated to the specified path
     *                                    could not be converted to boolean.
     * @since 2.0.0
     */
    boolean getBoolean(@NotNull String path, boolean def);


    /**
     * Returns a new list which contains all the boolean values mapped to {@code path}.
     *
     * @param path the path whose associated boolean values are to be returned.
     * @return a new list which contains all the boolean values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that could not
     *                                        be converted to boolean, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull Boolean> getBooleanList(@NotNull String path);


    /**
     * Returns the string representation of the value associated to the specified path.
     *
     * @param path the path whose associated string value is to be returned.
     * @return the string value associated to the specified path.
     * @throws NullPointerException if no mapping is present for the specified path.
     * @since 2.0.0
     */
    @NotNull
    String getString(@NotNull String path);


    /**
     * Returns the string representation of the value associated to the
     * specified path, or {@code def} if no mapping is present.
     *
     * @param path the path whose string representation of the associated value is to be returned.
     * @param def  the default string value to be returned if no mapping is present for the specified {@code path}.
     * @return the string representation of the value associated to the specified path,
     * or the given default string value if no mapping is present.
     * @since 2.0.0
     */
    @Nullable
    @Contract("_, !null -> !null")
    String getString(@NotNull String path, @Nullable String def);


    /**
     * Returns a new list which contains all the string representations of the values mapped to {@code path}.
     *
     * @param path the path whose string representations of the associated values are to be returned.
     * @return a new list which contains all the string representation of the values mapped to {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that has no string
     *                                        representation, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_ -> new")
    List<@NotNull String> getStringList(@NotNull String path);


    /**
     * Returns the enum value in {@code enumClass} associated to the specified path.
     *
     * @param <T>  the enum type whose constant is to be returned.
     * @param path the path whose associated enum constant in {@code enumClass} is to be returned.
     * @param enumClass the Class object of the enum type from which to return a constant.
     * @return the enum constant in {@code enumClass} associated to the specified path.
     * @throws NullPointerException       if no mapping is present for the specified path.
     * @throws ConfigurationTypeException if {@code enumClass} has no constant that matches the mapped value.
     * @since 2.0.0
     */
    @NotNull <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass);


    /**
     * Returns the enum constant in {@code enumClass} associated to the specified path,
     * or {@code def} if no mapping is present.
     *
     * @param <T>  the enum type whose constant is to be returned.
     * @param path the path whose associated enum constant in {@code enumClass} is to be returned.
     * @param enumClass the Class object of the enum type from which to return a constant.
     * @param def  the default enum constant in {@code enumClass} to be returned if no mapping is present for the specified {@code path}.
     * @return the enum constant in {@code enumClass} associated to the specified path,
     * or the given default constant value if no mapping is present.
     * @throws ConfigurationTypeException if {@code enumClass} has no constant
     *                                    that matches the mapped value, if any.
     * @since 2.0.0
     */
    @Nullable
    @Contract("_, _, !null -> !null")
    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass, @Nullable T def);


    /**
     * Returns a new list which contains all the enum constants
     * in {@code enumClass} associated to the specified {@code path}.
     *
     * @param <T>  the enum type whose constant is to be returned.
     * @param path the path whose associated enum constants in {@code enumClass} values are to be returned.
     * @param enumClass the Class object of the enum type from which to return a constant.
     * @return a new list which contains all the enum constants in {@code enumClass} associated to the specified {@code path}.
     * @throws NullPointerException           if no mapping is present for the specified path.
     * @throws ConfigurationListTypeException if the list contains any value that has no matching constant
     *                                        in {@code enumClass}, or any {@code null} value.
     * @since 2.0.0
     */
    @NotNull
    @Contract("_, _ -> new")
    <T extends Enum<T>> List<@NotNull T> getEnumList(@NotNull String path, @NotNull Class<T> enumClass);

}
