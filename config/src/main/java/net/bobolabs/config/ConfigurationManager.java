/*
 * This file is part of BoboLibs.
 *
 * Copyright (C) 2023 BoboLabs.net
 *
 * BoboLibs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoboLibs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoboLibs. If not, see <http://www.gnu.org/licenses/>.
 */

package net.bobolabs.config;

import com.google.common.base.Strings;
import net.bobolabs.utils.Reloadable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;

// A Glowy piace :D
public final class ConfigurationManager<T extends Enum<T> & ConfigurationDescriptor> implements Reloadable {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<T, Configuration> configurations;
    private final UnaryOperator<String> normalizer;
    private final File dataFolder;
    private final Class<T> clazz;

    public ConfigurationManager(@NotNull File dataFolder, @NotNull Class<T> clazz) {
        this(dataFolder, clazz, String::toLowerCase);
    }

    public ConfigurationManager(@NotNull File dataFolder,
                                @NotNull Class<T> clazz,
                                @NotNull UnaryOperator<@NotNull String> normalizer) {
        this.configurations = new EnumMap<>(clazz);
        this.clazz = clazz;
        this.dataFolder = dataFolder;
        this.normalizer = normalizer;
    }

    @Override
    public void onEnable() {
        lock.writeLock().lock();
        try {
            for (Field field : clazz.getFields()) {
                Config annotation = field.getDeclaredAnnotation(Config.class);
                T key = Enum.valueOf(clazz, field.getName());
                if (annotation != null) {
                    String path = annotation.path();
                    path = path.isEmpty() ? key.name() + ".yml" : path;
                    path = normalizer.apply(path);

                    String defaultResource = annotation.defaultResource();
                    defaultResource = Strings.isNullOrEmpty(defaultResource) ? path : defaultResource;

                    Configuration config = ConfigurationBuilder
                            .fromFile(dataFolder, path)
                            .saveDefaultFromResource(defaultResource)
                            .autoSave(annotation.autoSave())
                            .build();
                    configurations.put(key, config);
                } else {
                    // TODO: logger warn
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void onDisable() {
        lock.writeLock().lock();
        try {
            configurations.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public @NotNull Configuration configuration(@NotNull T config) {
        lock.readLock().lock();
        try {
            return configurations.get(config);
        } finally {
            lock.readLock().unlock();
        }
    }
}