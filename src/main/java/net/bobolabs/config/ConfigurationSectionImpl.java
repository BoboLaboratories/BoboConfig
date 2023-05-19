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

import net.bobolabs.core.Check;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

final class ConfigurationSectionImpl implements ConfigurationSection {

    private static final char SEPARATOR = '.';

    private final Map<String, Object> data;
    private final Configuration root;

    ConfigurationSectionImpl(@NotNull Configuration root, @Nullable Map<?, ?> ext) {
        this.data = new LinkedHashMap<>();
        this.root = root;

        if (ext != null) {
            root.writeLock().lock();
            try {
                for (Map.Entry<?, ?> entry : ext.entrySet()) {
                    String key = Objects.toString(entry.getKey());
                    if (entry.getValue() instanceof Map<?, ?> sectionData) {
                        data.put(key, new ConfigurationSectionImpl(root, sectionData));
                    } else {
                        data.put(key, entry.getValue());
                    }
                }
            } finally {
                root.writeLock().unlock();
            }
        }
    }

    //region get<Type>
    //--------------------------------------------------------------------------------------------------


    //--------------------------------------------------------------------------------------------------
    //endregion

    @Override
    public boolean contains(@NotNull String path) {
        // get already acquires lock
        return get(path, null) != null;
    }

    @Override
    public @NotNull Object get(@NotNull String path) {
        // get already acquires lock
        Object ret = get(path, null);
        return Objects.requireNonNull(ret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(@NotNull String path, @Nullable T def) {
        root.readLock().lock();
        try {
            Object ret = null;
            ConfigurationSection section = getSectionFor(path);
            if (section == this) {
                ret = data.get(path);
            } else if (section != null) {
                String subPath = getSubPath(path);
                ret = section.get(subPath, def);
            }
            return ret == null ? def : (T) ret;
        } finally {
            root.readLock().unlock();
        }
    }

    @Override
    public <T> @NotNull T getOrSet(@NotNull String path, @NotNull T value) {
        root.writeLock().lock();
        try {
            T ret = get(path, null);
            if (ret == null) {
                set(path, value);
                ret = value;
            }
            return ret;
        } finally {
            root.writeLock().unlock();
        }
    }

    @Override
    public @NotNull List<@Nullable Object> getList(@NotNull String path) {
        // lock needed to prevent concurrent modifications on the list while
        // copying using copy constructor (returned pointer points to the
        // same list that would be modified by write operations)
        root.readLock().lock();
        try {
            return (get(path) instanceof List<?> list) ? new ArrayList<>(list) : Collections.emptyList();
        } finally {
            root.readLock().unlock();
        }
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        root.writeLock().lock();
        try {
            if (value instanceof Map<?, ?> map) {
                value = new ConfigurationSectionImpl(root, map);
            }

            ConfigurationSection section = getSectionFor(path);
            if (section == this) {
                if (value == null) {
                    data.remove(path);
                } else {
                    if (value instanceof Enum<?> e) {
                        value = e.name();
                    }
                    data.put(path, value);
                }
            } else {
                int index = path.indexOf(SEPARATOR);
                String rootPath = path.substring(0, index);
                String subPath = path.substring(index + 1);

                if (section == null) {
                    section = new ConfigurationSectionImpl(root, null);
                    data.put(rootPath, section);
                }

                section.set(subPath, value);
            }

            root.autoSave();
        } finally {
            root.writeLock().unlock();
        }
    }

    @Override
    public void unset(@NotNull String path) {
        // set already acquires lock
        set(path, null);
    }

    @Override
    public @NotNull ConfigurationSection createSection(@NotNull String path) {
        if (contains(path)) {
            throw new IllegalArgumentException("path " + path + " already exists in this configuration section");
        }
        ConfigurationSection section = new ConfigurationSectionImpl(root, null);
        set(path, section);
        return section;
    }

    @Override
    public @NotNull ConfigurationSection getOrCreateSection(@NotNull String path) {
        ConfigurationSection section = getOptionalSection(path);
        if (section == null) {
            section = createSection(path);
        }
        return section;
    }

    @Override
    public @NotNull ConfigurationSection getSection(@NotNull String path) {
        // getOptionalSection already acquires lock
        ConfigurationSection section = getOptionalSection(path);
        return Objects.requireNonNull(section);
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def) {
        root.readLock().lock();
        try {
            ConfigurationSection ret = getSectionFor(path);
            String subPath = getSubPath(path);
            ConfigurationSection section = def;
            if (ret == this) {
                section = (ConfigurationSection) data.get(subPath);
            } else if (ret != null) {
                section = ret.getSection(subPath, def);
            }
            return section;
        } finally {
            root.readLock().unlock();
        }
    }

    @Override
    public @Nullable ConfigurationSection getOptionalSection(@NotNull String path) {
        // getSection already acquires lock
        return getSection(path, null);
    }

    @Override
    public @NotNull Set<@NotNull String> getKeys(@NotNull TraversalMode traversalMode) {
        root.readLock().lock();
        try {
            Set<String> accumulator = new HashSet<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (traversalMode != TraversalMode.LEAVES || !(entry.getValue() instanceof ConfigurationSection)) {
                    accumulator.add(entry.getKey());
                }
                if (traversalMode != TraversalMode.ROOT && entry.getValue() instanceof ConfigurationSection section) {
                    for (String subKey : section.getKeys(traversalMode)) {
                        accumulator.add(entry.getKey() + "." + subKey);
                    }
                }
            }
            return accumulator;
        } finally {
            root.readLock().unlock();
        }
    }

    @Contract("_, _,_, _, true -> !null")
    private <T, V> V getType(@NotNull String path,
                             @NotNull Class<T> superType,
                             @NotNull Class<V> exactType,
                             @NotNull Function<T, V> converter,
                             boolean throwIfNull) {
        // lock not needed as this method is supposed to be used
        // on unmodifiable types and get already acquires lock
        Object ret = get(path, null);
        if (superType.isInstance(ret)) {
            T value = superType.cast(ret);
            return converter.apply(value);
        } else if (ret == null && throwIfNull) {
            throw new NullPointerException("no mapping found for path `" + path + "` in configuration section");
        } else if (ret != null) {
            throw new ClassCastException(ret.getClass() + " could not be cast to " + exactType);
        }
        return null;
    }

    @Contract("_, _, _, _ -> new")
    private <T, V> @NotNull List<@NotNull V> getTypeList(@NotNull String path,
                                                         @NotNull Class<T> superType,
                                                         @NotNull Class<V> exactType,
                                                         @NotNull Function<T, V> converter) {
        List<V> list = new ArrayList<>();
        for (Object obj : getList(path)) {
            Objects.requireNonNull(obj);
            if (superType.isInstance(obj)) {
                T value = superType.cast(obj);
                V converted = converter.apply(value);
                list.add(converted);
            } else {
                System.out.println(obj);
                throw new ClassCastException(obj.getClass() + " could not be cast to " + exactType);
            }
        }
        return list;
    }


    @Override
    public byte getByte(@NotNull String path) {
        return getType(path, Integer.class, Byte.class, TypeConverters.BYTE, true);
    }

    @Override
    public byte getByte(@NotNull String path, byte def) {
        Byte ret = getType(path, Integer.class, Byte.class, TypeConverters.BYTE, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Byte> getByteList(@NotNull String path) {
        return getTypeList(path, Integer.class, Byte.class, TypeConverters.BYTE);
    }

    @Override
    public short getShort(@NotNull String path) {
        return getType(path, Integer.class, Short.class, TypeConverters.SHORT, true);
    }

    @Override
    public short getShort(@NotNull String path, short def) {
        Short ret = getType(path, Integer.class, Short.class, TypeConverters.SHORT, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Short> getShortList(@NotNull String path) {
        return getTypeList(path, Integer.class, Short.class, TypeConverters.SHORT);
    }

    @Override
    public int getInt(@NotNull String path) {
        return getType(path, Integer.class, Integer.class, TypeConverters.INTEGER, true);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        Integer ret = getType(path, Integer.class, Integer.class, TypeConverters.INTEGER, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Integer> getIntList(@NotNull String path) {
        return getTypeList(path, Integer.class, Integer.class, TypeConverters.INTEGER);
    }

    @Override
    public long getLong(@NotNull String path) {
        return getType(path, Object.class, Long.class, TypeConverters.LONG, true);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        Long ret = getType(path, Object.class, Long.class, TypeConverters.LONG, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Long> getLongList(@NotNull String path) {
        return getTypeList(path, Object.class, Long.class, TypeConverters.LONG);
    }

    @Override
    public float getFloat(@NotNull String path) {
        return getType(path, Number.class, Float.class, TypeConverters.FLOAT, true);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        Float ret = getType(path, Number.class, Float.class, TypeConverters.FLOAT, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Float> getFloatList(@NotNull String path) {
        return getTypeList(path, Number.class, Float.class, TypeConverters.FLOAT);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return getType(path, Number.class, Double.class, TypeConverters.DOUBLE, true);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        Double ret = getType(path, Number.class, Double.class, TypeConverters.DOUBLE, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Double> getDoubleList(@NotNull String path) {
        return getTypeList(path, Number.class, Double.class, TypeConverters.DOUBLE);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return getType(path, Boolean.class, Boolean.class, TypeConverters.BOOLEAN, true);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        Boolean ret = getType(path, Boolean.class, Boolean.class, TypeConverters.BOOLEAN, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull Boolean> getBooleanList(@NotNull String path) {
        return getTypeList(path, Boolean.class, Boolean.class, TypeConverters.BOOLEAN);
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        return getType(path, String.class, String.class, s -> s, true);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        String ret = getType(path, String.class, String.class, s -> s, false);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
        return getTypeList(path, String.class, String.class, s -> s);
    }

    @Override
    public <T extends Enum<T>> @NotNull T getEnum(@NotNull String path, @NotNull Class<T> enumClass) {
        T ret = getEnum(path, enumClass, null);
        return Objects.requireNonNull(ret);
    }

    @Override
    public <T extends Enum<T>> @Nullable T getEnum(@NotNull String path, @NotNull Class<T> enumClass, @Nullable T def) {
        // lock not needed as getString already worries about that and String is unmodifiable
        String str = getString(path, null);
        return str != null ? Enum.valueOf(enumClass, str) : def;
    }

    @Override
    public @NotNull <T extends Enum<T>> List<@NotNull T> getEnumList(@NotNull String path, @NotNull Class<T> enumClass) {
        // lock not needed as getStringList already worries about that and returns a new collection
        List<T> ret = new ArrayList<>();
        for (String str : getStringList(path)) {
            T entry = Enum.valueOf(enumClass, str);
            ret.add(entry);
        }
        return ret;
    }


    // ============================================
    //                   INTERNAL
    // ============================================

    @NotNull Map<String, Object> getData() {
        return data;
    }

    @NotNull Configuration getRoot() {
        return root;
    }

    private @Nullable ConfigurationSection getSectionFor(@NotNull String path) {
        root.readLock().lock();
        try {
            int index = path.indexOf(SEPARATOR);
            if (index == -1) {
                return this;
            } else {
                String rootPath = path.substring(0, index);
                return (ConfigurationSection) data.get(rootPath);
            }
        } finally {
            root.readLock().unlock();
        }
    }

    private @NotNull String getSubPath(@NotNull String path) {
        int index = path.indexOf(SEPARATOR);
        return index == -1 ? path : path.substring(index + 1);
    }

}
