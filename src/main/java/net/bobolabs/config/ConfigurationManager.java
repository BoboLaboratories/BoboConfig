/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Third party contributors
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * A thread-safe manager for multiple {@link Configuration} objects loaded
 * as instructed by the given {@link ConfigurationDescription} enum.
 * <p>
 * The same enum can then be used to perform various operations through this manager.
 *
 * @param <T> an enum that must implement {@link ConfigurationDescription} which is used to
 *            instruct the configuration manager on how to load configuration files.
 * @since 2.0.0
 */
// A Glowy piace :D
public final class ConfigurationManager<T extends Enum<T> & ConfigurationDescription> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<@NotNull T, @Nullable Configuration> configurations;
    private final File directory;
    private final Class<T> clazz;


    /**
     * Construct a new configuration managers which loads configurations from the
     * specified {@code directory} using the given {@code description} enum.
     *
     * @param directory   the directory inside which configuration files are to be loaded.
     * @param description the configuration description enum which provides instruction
     *                    on how configuration files are to be loaded.
     * @since 2.0.0
     */
    public ConfigurationManager(@NotNull File directory, @NotNull Class<T> description) {
        this.configurations = new EnumMap<>(description);
        this.directory = directory;
        this.clazz = description;
    }


    /**
     * Loads the specified {@link Configuration}, if not already loaded; then returns it.
     *
     * @param configuration the configuration to be loaded.
     * @return              the loaded configuration.
     * @since               2.0.0
     */
    public @NotNull Configuration load(@NotNull T configuration) {
        lock.writeLock().lock();
        try {
            Config config = makeConfig(configuration);
            ConfigurationLoader loader = ConfigurationLoader
                    .fromFile(directory, config.path())
                    .autoSave(config.autoSave());

            if (config.saveDefaultResource()) {
                loader.setDefaultResource(config.defaultResource());
            }

            Configuration loaded = loader.load();
            configurations.put(configuration, loaded);
            return loaded;
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Loads all {@link Configuration}s as specified by the given {@link ConfigurationDescription}.
     *
     * @since 2.0.0
     */
    public void loadAll() {
        lock.writeLock().lock();
        try {
            for (T configuration : clazz.getEnumConstants()) {
                load(configuration);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Unloads the specified {@link Configuration}, if already loaded.
     *
     * @param configuration the configuration to be unloaded.
     * @since 2.0.0
     */
    public void unload(@NotNull T configuration) {
        lock.writeLock().lock();
        try {
            configurations.remove(configuration);
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Unloads any previously loaded {@link Configuration}.
     *
     * @since 2.0.0
     */
    public void unloadAll() {
        lock.writeLock().lock();
        try {
            configurations.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Returns the specified optional {@link Configuration}, if loaded;
     * or an empty {@link Optional} otherwise.
     *
     * @param configuration the optional configuration that is to be returned if loaded.
     * @return the specified optional {@link Configuration}, if loaded.
     * @since 2.0.0
     */
    public @NotNull Optional<Configuration> getOptional(@NotNull T configuration) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(configurations.get(configuration));
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Returns the specified {@link Configuration} if loaded; otherwise returns the given default one.
     *
     * @param configuration the configuration that is to be returned.
     * @param def           the default configuration that is to be returned if the specified one is not loaded.
     * @return the specified {@link Configuration} if loaded; or the given default one if not.
     * @since 2.0.0
     */
    public @NotNull Configuration get(@NotNull T configuration, @NotNull Configuration def) {
        // getOptional already acquires lock
        return getOptional(configuration).orElse(def);
    }


    /**
     * Returns the specified {@link Configuration} if loaded, the given default one if the
     * first is not loaded, or throws {@link NullPointerException} if none is loaded.
     *
     * @param configuration the configuration that is to be returned.
     * @param def           the default configuration that is to be returned if the specified one is not loaded.
     * @return the specified {@link Configuration} if loaded; or the given default one if not.
     * @throws NullPointerException if none of the configurations are loaded when this method is invoked.
     * @since 2.0.0
     */
    public @NotNull Configuration get(@NotNull T configuration, @NotNull T def) {
        // read lock needed as two get operations could be performed and must be performed atomically
        lock.readLock().lock();
        try {
            Optional<Configuration> opt = getOptional(configuration);
            return opt.orElseGet(() -> get(def));
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Returns the specified {@link Configuration} if loaded; otherwise throws {@link NullPointerException}.
     *
     * @param configuration the configuration that is to be returned.
     * @return the specified {@link Configuration} if loaded.
     * @throws NullPointerException if the specified configuration is not loaded when this method is invoked.
     * @since 2.0.0
     */
    public @NotNull Configuration get(@NotNull T configuration) {
        // getOptional already acquires lock
        return getOptional(configuration).orElseThrow(() ->
                new NullPointerException("configuration " + configuration + " was not loaded"));
    }


    // ============================================
    //                   INTERNAL
    // ============================================

    @NotNull Config makeConfig(@NotNull T configuration) {
        try {
            Field field = clazz.getField(configuration.name());
            Config annotation = field.getDeclaredAnnotation(Config.class);
            return new Config() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Config.class;
                }

                @Override
                public @NotNull String path() {
                    String path = annotation != null ? annotation.path() : ConfigDefaults.PATH;
                    return path.isBlank() ? configuration.name().toLowerCase() + ".yml" : path;
                }

                @Override
                public @NotNull String defaultResource() {
                    String defaultResource = annotation != null ? annotation.defaultResource() : ConfigDefaults.RESOURCE;
                    return defaultResource.isBlank() ? path() : defaultResource;
                }

                @Override
                public boolean autoSave() {
                    return annotation != null ? annotation.autoSave() : ConfigDefaults.AUTO_SAVE;
                }

                @Override
                public boolean saveDefaultResource() {
                    return annotation != null ? annotation.saveDefaultResource() : ConfigDefaults.SAVE_DEFAULT_RESOURCE;
                }
            };
        } catch (NoSuchFieldException e) {
            throw new NullPointerException(); // TODO
        }
    }

}