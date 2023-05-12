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

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Configuration implements ConfigurationSection {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ConfigurationProvider provider;
    private final boolean saveDefaultResource;
    private final boolean autoSave;
    private final File file;

    private ConfigurationSectionImpl section;

    Configuration(@NotNull File file, @Nullable String defaultResource, boolean saveDefaultResource, boolean autoSave) {
        this.provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        this.saveDefaultResource = saveDefaultResource;
        this.autoSave = autoSave;
        this.file = file;

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            if (saveDefaultResource) {
                if (defaultResource == null) {
                    defaultResource = file.getName();
                }
                try (InputStream in = getClass().getResourceAsStream("/" + defaultResource)) {
                    if (in != null) {
                        Files.copy(in, file.toPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO: cabbo vuoi da me?
            }
        }

        load();
    }

    @Override
    public boolean contains(@NotNull String path) {
        return section.contains(path);
    }

    @Override
    public Object get(@NotNull String path) {
        return section.get(path);
    }

    @Override
    public <T> T get(@NotNull String path, T def) {
        return section.get(path, def);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        section.set(path, value);
    }

    @Override
    public ConfigurationSection getSection(@NotNull String path) {
        return section.getSection(path);
    }

    @Override
    public @NotNull Collection<String> getKeys() {
        return section.getKeys();
    }

    @Override
    public byte getByte(@NotNull String path) {
        return section.getByte(path);
    }

    @Override
    public byte getByte(@NotNull String path, byte def) {
        return section.getByte(path, def);
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        return section.getByteList(path);
    }

    @Override
    public short getShort(@NotNull String path) {
        return section.getShort(path);
    }

    @Override
    public short getShort(@NotNull String path, short def) {
        return section.getShort(path, def);
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) {
        return section.getShortList(path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return section.getInt(path);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return section.getInt(path, def);
    }

    @Override
    public @NotNull List<Integer> getIntList(@NotNull String path) {
        return section.getIntList(path);
    }

    @Override
    public long getLong(@NotNull String path) {
        return section.getLong(path);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return section.getLong(path, def);
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        return section.getLongList(path);
    }

    @Override
    public float getFloat(@NotNull String path) {
        return section.getFloat(path);
    }

    @Override
    public float getFloat(@NotNull String path, float def) {
        return section.getFloat(path, def);
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        return section.getFloatList(path);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return section.getDouble(path);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return section.getDouble(path, def);
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        return section.getDoubleList(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return section.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return section.getBoolean(path, def);
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        return section.getBooleanList(path);
    }

    @Override
    public char getChar(@NotNull String path) {
        return section.getChar(path);
    }

    @Override
    public char getChar(@NotNull String path, char def) {
        return section.getChar(path, def);
    }

    @Override
    public @NotNull List<Character> getCharList(@NotNull String path) {
        return section.getCharList(path);
    }

    @Override
    public String getString(@NotNull String path) {
        return section.getString(path);
    }

    @Override
    public String getString(@NotNull String path, String def) {
        return section.getString(path, def);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        return section.getStringList(path);
    }

    @Override
    public @NotNull List<?> getList(@NotNull String path) {
        return section.getList(path);
    }

    @Override
    public @NotNull List<?> getList(@NotNull String path, List<?> def) {
        return section.getList(path, def);
    }

    @Override
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> enumClass) {
        return section.getEnum(path, enumClass);
    }

    public void save() {
        getWriteLock().lock();
        try {
            provider.save(section.getRaw(), file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            getWriteLock().unlock();
        }
    }

    private void load() {
        getWriteLock().lock();
        try {
            net.md_5.bungee.config.Configuration raw = provider.load(file);
            section = new ConfigurationSectionImpl(raw, this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            getWriteLock().unlock();
        }
    }

    public void reload() {
        load();
    }

    void autoSave() {
        if (autoSave) {
            save();
        }
    }

    ReentrantReadWriteLock.ReadLock getReadLock() {
        return lock.readLock();
    }

    ReentrantReadWriteLock.WriteLock getWriteLock() {
        return lock.writeLock();
    }

}
