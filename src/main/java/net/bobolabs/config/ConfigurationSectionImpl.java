/*
 * This file is part of BoboLabs - BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Third party contributors
 *
 * BoboLabs - BoboConfig is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoboLabs - BoboConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoboLabs - BoboConfig.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bobolabs.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

final class ConfigurationSectionImpl implements ConfigurationSection {

    private final net.md_5.bungee.config.Configuration section;
    private final Configuration config;

    ConfigurationSectionImpl(@NotNull net.md_5.bungee.config.Configuration section, @NotNull Configuration config) {
        this.section = section;
        this.config = config;
    }

    @Override
    public boolean contains(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.contains(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public Object get(@NotNull String path) {
        config.getReadLock().lock();
        try {
            return section.get(path);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public <T> T get(@NotNull String path, T def) {
        config.getReadLock().lock();
        try {
            return section.get(path, def);
        } finally {
            config.getReadLock().unlock();
        }
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        config.getWriteLock().lock();
        try {
            section.set(path, value);
            config.autoSave();
        } finally {
            config.getWriteLock().unlock();
        }
    }

    @Override
    public ConfigurationSection getSection(@NotNull String path) {
        config.getReadLock().lock();
        try {
            net.md_5.bungee.config.Configuration newSection = section.getSection(path);
            return new ConfigurationSectionImpl(newSection, config);
        } finally {
            config.getReadLock().unlock();
        }
    }

    private @NotNull Collection<String> getDeepKeyList(@NotNull ConfigurationSection config,
                                                       @NotNull HashSet<String> result,
                                                       @NotNull String resolvedKey) {
        Collection<String> keys = config.getKeys(false);
        for (String key : keys) {
            Object object = config.get(key);
            if (object instanceof net.md_5.bungee.config.Configuration) {
                ConfigurationSection keySection = config.getSection(key);
                if (!key.isEmpty()) {
                    key += ".";
                }
                result.addAll(getDeepKeyList(keySection, result, resolvedKey + key));
            } else {
                result.add(resolvedKey + key);
            }
        }
        return result;
    }

    @Override
    public @NotNull Collection<String> getKeys(boolean deep) {
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

}
