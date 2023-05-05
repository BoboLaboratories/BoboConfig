/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
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

package config;

import com.google.common.base.Strings;
import net.bobolabs.core.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;

// A Glowy piace :D
public final class ConfigurationManager<T extends Enum<T> & ConfigurationDescription> implements Reloadable {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<@NotNull T, @Nullable Configuration> configurations;
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
    public void enable() {
        lock.writeLock().lock();
        try {
            for (Field field : clazz.getFields()) {
                T key = Enum.valueOf(clazz, field.getName());

                // Retrieve annotation data
                Config annotation = field.getDeclaredAnnotation(Config.class);
                if (annotation == null) {
                    annotation = DEFAULT_CONFIG_ANNOTATION;
                }

                // Compute path
                String path = annotation.path();
                path = path.isEmpty() ? key.name() + ".yml" : path;
                path = normalizer.apply(path);

                // Compute default resource
                String defaultResource = annotation.defaultResource();
                defaultResource = Strings.isNullOrEmpty(defaultResource) ? path : defaultResource;

                // Build configuration
                Configuration configuration = ConfigurationBuilder
                        .fromFile(dataFolder, path)
                        .saveDefaultResource(annotation.saveDefaultResource())
                        .setDefaultResource(defaultResource)
                        .autoSave(annotation.autoSave())
                        .build();

                configurations.put(key, configuration);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void disable() {
        lock.writeLock().lock();
        try {
            configurations.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public @Nullable Configuration optionalConfiguration(@NotNull T config) {
        lock.readLock().lock();
        try {
            return configurations.get(config);
        } finally {
            lock.readLock().unlock();
        }
    }

    public @NotNull Configuration configuration(@NotNull T config) {

//        lock.readLock().lock();
//        try {
//            return configurations.get(config);
//        } finally {
//            lock.readLock().unlock();
//        }
    }


    private static final Config DEFAULT_CONFIG_ANNOTATION = new Config() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Config.class;
        }

        @Override
        public String path() {
            return ConfigDefaults.PATH;
        }

        @Override
        public String defaultResource() {
            return ConfigDefaults.RESOURCE;
        }

        @Override
        public boolean autoSave() {
            return ConfigDefaults.AUTO_SAVE;
        }

        @Override
        public boolean saveDefaultResource() {
            return ConfigDefaults.SAVE_DEFAULT_RESOURCE;
        }
    };

}