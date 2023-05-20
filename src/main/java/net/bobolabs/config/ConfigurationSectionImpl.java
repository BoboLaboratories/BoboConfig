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

import java.util.*;
import java.util.function.Function;

final class ConfigurationSectionImpl implements ConfigurationSection {

    private static final Map<Class<?>, Function<Object, ?>> mappers = new HashMap<>();

    static {
        // numeric types
        mappers.put(Byte.class,     o -> (o instanceof Number n) ? n.byteValue() : null);
        mappers.put(Short.class,    o -> (o instanceof Number n) ? n.shortValue() : null);
        mappers.put(Integer.class,  o -> (o instanceof Number n) ? n.intValue() : null);
        mappers.put(Long.class,     o -> (o instanceof Number n) ? n.longValue() : null);
        mappers.put(Float.class,    o -> (o instanceof Number n) ? n.floatValue() : null);
        mappers.put(Double.class,   o -> (o instanceof Number n) ? n.doubleValue() : null);

        // special types
        mappers.put(Boolean.class,  o -> (o instanceof Boolean b) ? b : null);
        mappers.put(String.class,   o -> (o != null) ? Objects.toString(o) : null);
    }

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

    @Override
    public boolean contains(@NotNull String path) {
        // get already acquires lock
        return get(path, null, false) != null;
    }

    @Override
    public @NotNull Object get(@NotNull String path) {
        // get already acquires lock
        return get(path, null, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(@NotNull String path, @Nullable T def) {
        return get(path, def, false);
    }

//    @Override
//    @SuppressWarnings("unchecked")
//    public <T> @NotNull T getOrSet(@NotNull String path, @NotNull T value) {
//        root.writeLock().lock();
//        try {
//            T ret;
//            Object obj = get(path, null);
//            if (obj != null) {
//                try {
//                    ret = (T) value.getClass().cast(obj);
//                } catch (ClassCastException ignored) {
//                    throw new ConfigurationTypeException(path, value.getClass(), obj);
//                }
//            } else {
//                set(path, value);
//                ret = value;
//            }
//            return ret;
//        } finally {
//            root.writeLock().unlock();
//        }
//    }

    @Override
    public @NotNull List<@Nullable Object> getList(@NotNull String path) {
        // lock needed to prevent concurrent modifications on the list while
        // copying using copy constructor (returned pointer points to the
        // same list that would be modified by write operations)
        root.readLock().lock();
        try {
            return (get(path, null, true) instanceof List<?> list) ? new ArrayList<>(list) : Collections.emptyList();
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
        root.writeLock().lock();
        try {
            if (contains(path)) {
                throw new IllegalArgumentException("path `" + path + "` already exists in this configuration section");
            }
            ConfigurationSection section = new ConfigurationSectionImpl(root, null);
            set(path, section);
            return section;
        } finally {
            root.writeLock().unlock();
        }
    }

    @Override
    public @NotNull ConfigurationSection getOrCreateSection(@NotNull String path) {
        root.writeLock().lock();
        try {
            ConfigurationSection section = getSection(path, path, null, false);
            if (section == null) {
                section = createSection(path);
            }
            return section;
        } finally {
            root.writeLock().unlock();
        }
    }

    @Override
    public @NotNull ConfigurationSection getSection(@NotNull String path) {
        // getOptionalSection already acquires lock
        return getSection(path, path, null, true);
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def) {
        return getSection(path, path, def, false);
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

    @Override
    public byte getByte(@NotNull String path) {
        return getType(path, Byte.class);
    }

    @Override
    public byte getByte(@NotNull String path, byte def) {
        return getType(path, Byte.class, def);
    }

    @Override
    public @NotNull List<@NotNull Byte> getByteList(@NotNull String path) {
        return getTypeList(path, Byte.class);
    }

    @Override
    public short getShort(@NotNull String path) {
        return getType(path, Short.class);
    }

    @Override
    public short getShort(@NotNull String path, short def) {
        return getType(path, Short.class, def);
    }

    @Override
    public @NotNull List<@NotNull Short> getShortList(@NotNull String path) {
        return getTypeList(path, Short.class);
    }

    @Override
    public int getInt(@NotNull String path) {
        return getType(path, Integer.class);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return getType(path, Integer.class, def);
    }

    @Override
    public @NotNull List<@NotNull Integer> getIntList(@NotNull String path) {
        return getTypeList(path, Integer.class);
    }

    @Override
    public long getLong(@NotNull String path) {
        return getType(path, Long.class);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return getType(path, Long.class, def);
    }

    @Override
    public @NotNull List<@NotNull Long> getLongList(@NotNull String path) {
        return getTypeList(path, Long.class);
    }

    @Override
    public float getFloat(@NotNull String path) {
        return getType(path, Float.class);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        return getType(path, Float.class, def);
    }

    @Override
    public @NotNull List<@NotNull Float> getFloatList(@NotNull String path) {
        return getTypeList(path, Float.class);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return getType(path, Double.class);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return getType(path, Double.class, def);
    }

    @Override
    public @NotNull List<@NotNull Double> getDoubleList(@NotNull String path) {
        return getTypeList(path, Double.class);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return getType(path, Boolean.class);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return getType(path, Boolean.class, def);
    }

    @Override
    public @NotNull List<@NotNull Boolean> getBooleanList(@NotNull String path) {
        return getTypeList(path, Boolean.class);
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        return getType(path, String.class);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return getType(path, String.class, def);
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
        return getTypeList(path, String.class);
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

    @SuppressWarnings("unchecked")
    @Contract("_, !null, _ -> !null; _, _, true -> !null")
    private  <T> @Nullable T get(@NotNull String path, @Nullable T def, boolean throwIfNull) {
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
            if (ret == null && throwIfNull) {
                throw new NullPointerException("no mapping found for path `" + path + "` in configuration section");
            }
            return ret == null ? def : (T) ret;
        } finally {
            root.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private @NotNull <T> Function<Object, @Nullable T> getMapperFor(@NotNull Class<T> type) {
        return (Function<Object, @Nullable T>) mappers.get(type);
    }    private <T> @NotNull T getType(@NotNull String path, @NotNull Class<T> type) {
        Object obj = get(path);
        T ret = getMapperFor(type).apply(obj);
        if (ret == null) {
            throw new ConfigurationTypeException(path, type, obj);
        }
        return ret;
    }

    @Contract("_, _, !null -> !null")
    private <T> T getType(@NotNull String path, @NotNull Class<T> type, @Nullable T def) {
        Object obj = get(path, null);
        if (obj == null) {
            return def;
        } else {
            T ret = getMapperFor(type).apply(obj);
            if (ret == null) {
                throw new ConfigurationTypeException(path, type, obj);
            }
            return ret;
        }
    }

    @Contract("_, _ -> new")
    private <T> @NotNull List<@NotNull T> getTypeList(@NotNull String path, @NotNull Class<T> type) {
        List<T> list = new ArrayList<>();
        List<Object> ret = getList(path);
        for (Object obj : ret) {
            T value = getMapperFor(type).apply(obj);
            if (value != null) {
                list.add(value);
            } else {
                throw new ConfigurationListTypeException(path, type, ret, obj);
            }
        }
        return list;
    }

    @Contract("_, _, !null, _ -> !null; _, _, _, true -> !null")
    private @Nullable ConfigurationSection getSection(@NotNull String rootPath,
                                                      @NotNull String relativePath,
                                                      @Nullable ConfigurationSection def,
                                                      boolean throwIfNull) {
        root.readLock().lock();
        try {
            Object section = null;
            ConfigurationSectionImpl ret = getSectionFor(relativePath);
            String subPath = getSubPath(relativePath);
            if (ret == this) {
                section = data.get(subPath);
            } else if (ret != null) {
                section = ret.getSection(rootPath, subPath, def, false);
            }
            if (section != null) {
                try {
                    return (ConfigurationSection) section;
                } catch (ClassCastException ignored) {
                    throw new ConfigurationTypeException(rootPath, ConfigurationSection.class, section);
                }
            } else if (throwIfNull) {
                throw new NullPointerException("no mapping found for path `" + rootPath + "` in configuration section");
            }
            return def;
        } finally {
            root.readLock().unlock();
        }
    }

    private @Nullable ConfigurationSectionImpl getSectionFor(@NotNull String path) {
        root.readLock().lock();
        try {
            int index = path.indexOf(SEPARATOR);
            if (index == -1) {
                return this;
            } else {
                String rootPath = path.substring(0, index);
                return (ConfigurationSectionImpl) data.get(rootPath);
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
