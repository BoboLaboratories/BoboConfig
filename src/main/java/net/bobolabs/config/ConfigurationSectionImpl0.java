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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// TODO locks
public class ConfigurationSectionImpl0 implements ConfigurationSection {

    private static final char SEPARATOR = '.';

    private final ReentrantReadWriteLock lock;
    private final Map<String, Object> data;

    ConfigurationSectionImpl0(@NotNull Map<?, ?> data) {
        this(data, new ReentrantReadWriteLock());
    }

    ConfigurationSectionImpl0(@NotNull Map<?, ?> ext, @NotNull ReentrantReadWriteLock lock) {
        this.data = new LinkedHashMap<>();
        this.lock = lock;

        for (Map.Entry<?, ?> entry : ext.entrySet()) {
            String key = entry.getKey().toString();
            if (entry.getValue() instanceof Map<?, ?> section) {
                data.put(key, new ConfigurationSectionImpl0(section, lock));
            } else {
                data.put(key, entry.getValue());
            }
        }
    }

    @Override
    public boolean contains(@NotNull String path) {
        return get(path) != null;
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        return get(path, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(@NotNull String path, @Nullable T def) {
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
    public @NotNull List<@NotNull Object> getList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public @NotNull List<@NotNull Object> getList(@NotNull String path, @NotNull List<Object> def) {
        return null; // TODO
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        if (value instanceof Map<?, ?> map) {
            value = new ConfigurationSectionImpl0(map, lock);
        }

        ConfigurationSection section = getSectionFor(path);
        if (section == this) {
            if (value == null) {
                data.remove(path);
            } else {
                data.put(path, value);
            }
        } else {
            section.get(getSubPath(path), value); // TODO
        }
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return getSection(path, null);
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path, @Nullable ConfigurationSection def) {
        ConfigurationSection ret = getSectionFor(path);
        return ret != null ? ret : def;
    }

    @Override
    public @NotNull Collection<@NotNull String> getKeys(boolean deep) {
        return null;
    }

    @Override
    public byte getByte(@NotNull String path) {
        return getByte(path, (byte) 0);
    }

    @Override
    public byte getByte(@NotNull String path, byte def) {
        Object ret = get(path);
        return (ret instanceof Number n) ? n.byteValue() : def;
    }

    @Override
    public @NotNull List<@NotNull Byte> getByteList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public short getShort(@NotNull String path) {
        return getShort(path, (short) 0);
    }

    @Override
    public short getShort(@NotNull String path, short def) {
        Object ret = get(path);
        return (ret instanceof Number n) ? n.shortValue() : def;
    }

    @Override
    public @NotNull List<@NotNull Short> getShortList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        Object ret = get(path);
        return (ret instanceof Number n) ? n.intValue() : def;
    }

    @Override
    public @NotNull List<@NotNull Integer> getIntList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        Object ret = get(path);
        return (ret instanceof Number n) ? n.longValue() : def;
    }

    @Override
    public @NotNull List<@NotNull Long> getLongList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public float getFloat(@NotNull String path) {
        return getFloat(path, 0);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        Object ret = get(path);
        return (ret instanceof Number n) ? n.floatValue() : def;
    }

    @Override
    public @NotNull List<@NotNull Float> getFloatList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        Object ret = get(path);
        return (ret instanceof Number n) ? n.doubleValue() : def;
    }

    @Override
    public @NotNull List<@NotNull Double> getDoubleList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        Object ret = get(path);
        return (ret instanceof Boolean bool) ? bool : def;
    }

    @Override
    public @NotNull List<@NotNull Boolean> getBooleanList(@NotNull String path) {
        return null;
    }

//    @Override
//    public char getChar(@NotNull String path) {
//        return getChar(path, '\u0000');
//    }

//    @Override
//    public char getChar(@NotNull String path, char def) {
//        Object ret = get(path);
//        System.out.println(ret.getClass());
//        return (ret instanceof Character ch) ? ch : def;
//    }
//
//    @Override
//    public @NotNull List<@NotNull Character> getCharList(@NotNull String path) {
//        return null; // TODO
//    }

    @Override
    public @Nullable String getString(@NotNull String path) {
        return getString(path, null);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        Object ret = get(path);
        return (ret instanceof String str) ? str : def;
    }

    @Override
    public @NotNull List<@NotNull String> getStringList(@NotNull String path) {
        return null; // TODO
    }

    @Override
    public <T extends Enum<T>> @Nullable T getEnum(@NotNull String path, @NotNull Class<T> enumClass) {
        return getEnum(path, enumClass, null);
    }

    @Override
    public <T extends Enum<T>> @Nullable T getEnum(@NotNull String path, @NotNull Class<T> enumClass, @Nullable T def) {
        String str = getString(path);
        return (str != null) ?  Enum.valueOf(enumClass, str) : def;
    }

    @Override
    public @NotNull <T extends Enum<T>> List<@NotNull T> getEnumList(@NotNull String path, @NotNull Class<T> enumClass) {
        return null; // TODO
    }


    // ============================================
    //                   INTERNAL
    // ============================================

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

    private @NotNull String getSubPath(@NotNull String path) {
        int index = path.indexOf(SEPARATOR);
        return (index == -1) ? path : path.substring(index + 1);
    }

}
