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

final class ConfigurationSectionImpl { /* implements ConfigurationSection {

    private static final char SEPARATOR = '.';

    private final ReentrantReadWriteLock lock;
    private final Map<String, Object> data;

    ConfigurationSectionImpl(@NotNull Map<?, ?> data) {
        this(data, new ReentrantReadWriteLock());
    }

    ConfigurationSectionImpl(@NotNull Map<?, ?> ext, @NotNull ReentrantReadWriteLock lock) {
        this.data = new LinkedHashMap<>();
        this.lock = lock;

        for (Map.Entry<?, ?> entry : ext.entrySet()) {
            String key = entry.getKey().toString();
            if (entry.getValue() instanceof Map<?, ?> section) {
                data.put(key, new ConfigurationSectionImpl(section, lock));
            } else {
                data.put(key, entry.getValue());
            }
        }
    }

    private @NotNull String getSubPath(@NotNull String path) {
        int index = path.indexOf(SEPARATOR);
        return (index == -1) ? path : path.substring(index + 1);
    }

    @Override
    public boolean contains(@NotNull String path) {
        return get(path) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull String path, @Nullable T def) {
        lock.readLock().lock();
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
            lock.readLock().unlock();
        }
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
//        if (value instanceof Map<?, ?> map) {
//            value = new ConfigurationSectionImpl(map, lock);
//        }
//
//        ConfigurationSection section = getSectionFor(path);
//        if (section == this) {
//            if (value == null) {
//                data.remove(path);
//            } else {
//                data.put(path, value);
//            }
//        } else {
//            section.get(getSubPath(path), value);
//        }
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return getSectionFor(path);
    }

    private @Nullable ConfigurationSection getSectionFor(@NotNull String path) {
        lock.readLock().lock();
        try {
            int index = path.indexOf(SEPARATOR);
            if (index == -1) {
                return this;
            } else {
                String rootPath = path.substring(0, index);
                return (ConfigurationSection) data.get(rootPath);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

//    private @NotNull Collection<String> getDeepKeyList(@NotNull ConfigurationSection config,
//                                                       @NotNull HashSet<String> result,
//                                                       @NotNull String resolvedKey) {
//        Collection<String> keys = config.getKeys(false);
//        for (String key : keys) {
//            Object object = config.get(key);
//            if (object instanceof net.md_5.bungee.config.Configuration) {
//                ConfigurationSection keySection = config.getSection(key);
//                if (!key.isEmpty()) {
//                    key += ".";
//                }
//                result.addAll(getDeepKeyList(keySection, result, resolvedKey + key));
//            } else {
//                result.add(resolvedKey + key);
//            }
//        }
//        return result;
//    }

    @Override
    public @NotNull Collection<@NotNull String> getKeys(boolean deep) {
        config.getReadLock().lock();
        try {
            if (!deep) {
                return section.getKeys();
            } else {
                return getDeepKeyList(config, new HashSet<>(), "");
            }
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public byte getByte(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getByte(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public byte getByte(@NotNull String path, byte def) {
        config.getReadLock().lock();
        try {
            return section.getByte(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getByteList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public short getShort(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getShort(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public short getShort(@NotNull String path, short def) {
        config.getReadLock().lock();
        try {
            return section.getShort(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Short> getShortList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getShortList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public int getInt(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getInt(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        config.getReadLock().lock();
        try {
            return section.getInt(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Integer> getIntList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getIntList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public long getLong(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getLong(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        config.getReadLock().lock();
        try {
            return section.getLong(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Long> getLongList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getLongList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public float getFloat(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getFloat(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        config.getReadLock().lock();
        try {
            return section.getFloat(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getFloatList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public double getDouble(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getDouble(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        config.getReadLock().lock();
        try {
            return section.getDouble(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getDoubleList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getBoolean(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        config.getReadLock().lock();
        try {
            return section.getBoolean(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getBooleanList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public char getChar(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getChar(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public char getChar(@NotNull String path, char def) {
        config.getReadLock().lock();
        try {
            return section.getChar(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<Character> getCharList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getCharList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public String getString(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getString(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public String getString(@NotNull String path, String def) {
        config.getReadLock().lock();
        try {
            return section.getString(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    @Contract("_ -> new")
    public @NotNull List<String> getStringList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getStringList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public @NotNull List<?> getList(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.getList(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public @NotNull List<?> getList(@NotNull String path, List<?> def) {
        config.getReadLock().lock();
        try {
            return section.getList(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass) {
        String raw = getString(path); // already acquires lock
        return Enum.valueOf(enumClass, raw);
    }

    net.md_5.bungee.config.Configuration getRaw() {
        return section;
    }
*/
}
