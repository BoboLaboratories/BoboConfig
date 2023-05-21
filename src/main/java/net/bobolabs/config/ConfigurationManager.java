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

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
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
     * @param directory the directory inside which configuration files are to be loaded.
     * @param description the configuration description enum which provides instruction
     *                    on how configuration files are to be loaded.
     * @since             2.0.0
     */
    public ConfigurationManager(@NotNull File directory, @NotNull Class<T> description) {
        this.configurations = new EnumMap<>(description);
        this.directory = directory;
        this.clazz = description;
    }


    /**
     * Loads all {@link Configuration}s as specified by the given {@link ConfigurationDescription}.
     *
     * @since 2.0.0
     */
    public void loadAll() {
        lock.writeLock().lock();
        try {
            for (Field field : clazz.getFields()) {
                T key = Enum.valueOf(clazz, field.getName());
                load(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Loads the specified {@link Configuration}, if not already loaded; then returns it.
     *
     * @param configuration the configuration to be loaded.
     * @since               2.0.0
     */
    public @NotNull Configuration load(@NotNull T configuration) {
        lock.writeLock().lock();
        try {
            // Retrieve annotation data
            Field field = clazz.getField(configuration.name());
            Config annotation = field.getDeclaredAnnotation(Config.class);
            if (annotation == null) {
                annotation = DEFAULT_CONFIG_ANNOTATION;
            }

            // Compute path
            String path = annotation.path();
            path = path.isEmpty() ? configuration.name() + ".yml" : path;
            // path = normalizer.apply(path);

            // Compute default resource
            String defaultResource = annotation.defaultResource();
            // TODO defaultResource = Strings.isNullOrEmpty(defaultResource) ? path : defaultResource;

            // Build configuration
            Configuration config = ConfigurationLoader
                    .fromFile(directory, path)
                    .setDefaultResource(annotation.defaultResource())
                    .setDefaultResource(defaultResource)
                    .autoSave(annotation.autoSave())
                    .load();

            configurations.put(configuration, config);
            return config;

        } catch (NoSuchFieldException e) {
            throw new ConfigurationException("could not load configuration " + configuration, e);
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Unloads the specified {@link Configuration}, if already loaded.
     *
     * @param configuration the configuration to be unloaded.
     * @since               2.0.0
     */
    public void unload(@NotNull T configuration) {
        lock.writeLock().lock();
        try {
            Optional<Configuration> config = getOptional(configuration);
            if (config.isPresent()) {
                configurations.remove(configuration);
            }
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
     * Reloads the specified {@link Configuration}, if already loaded; or loads it otherwise.
     * <p>
     * Note that, if the configuration is already loaded, this method has the same effect as
     * {@code configurationManager.get(CONFIG).reload();} except it also returns the configuration itself.
     *
     * @param configuration the configuration to be reloaded.
     * @since               2.0.0
     */
    public @NotNull Configuration reload(@NotNull T configuration) {
        unload(configuration);
        return load(configuration);
    }


    /**
     * Reloads any previously loaded {@link Configuration}.
     *
     * @since 2.0.0
     */
    public void reloadAll() {
        lock.writeLock().lock();
        try {
            Set<T> configs = new HashSet<>();
            for (T key : configurations.keySet()) {
                configs.add(key);
                unload(key);
            }

            for (T config : configs) {
                load(config);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Saves the specified {@link Configuration}, if loaded; otherwise throws {@link IllegalStateException}.
     * <p>
     * Note that this method has the same effect as {@code configurationManager.get(CONFIG).save();}
     * except it also returns the configuration itself.
     *
     * @param configuration the configuration to be reloaded.
     * @throws              IllegalStateException if the specified configuration is not
     *                                            loaded when this method is invoked.
     * @since               2.0.0
     */
    public @NotNull Configuration save(@NotNull T configuration) {
        lock.writeLock().lock();
        try {
            Configuration config = get(configuration);
            config.save();
            return config;
        } catch (NullPointerException e) {
            throw new IllegalStateException("the specified configuration was not loaded");
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Saves any loaded {@link Configuration}.
     *
     * @since 2.0.0
     */
    public void saveAll() {
        lock.writeLock().lock();
        try {
            for (T key : configurations.keySet()) {
                save(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Returns the specified optional {@link Configuration}, if loaded;
     * or an empty {@link Optional} otherwise.
     *
     * @param configuration the optional configuration that is to be returned if loaded.
     * @return              the specified optional {@link Configuration}, if loaded.
     * @since               2.0.0
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
     * @return              the specified {@link Configuration} if loaded; or the given default one if not.
     * @since               2.0.0
     */
    public @NotNull Configuration get(@NotNull T configuration, @NotNull Configuration def) {
        return getOptional(configuration).orElse(def);
    }


    /**
     * Returns the specified {@link Configuration} if loaded, the given default one if the
     * first is not loaded, or throws {@link NullPointerException} if none is loaded.
     *
     * @param configuration the configuration that is to be returned.
     * @param def           the default configuration that is to be returned if the specified one is not loaded.
     * @return              the specified {@link Configuration} if loaded; or the given default one if not.
     * @throws              NullPointerException if none of the configurations are loaded when this method is invoked.
     * @since               2.0.0
     */
    public @NotNull Configuration get(@NotNull T configuration, @NotNull T def) {
        lock.readLock().lock();
        try {
            Optional<Configuration> config = getOptional(configuration);
            Configuration defaultConfig = get(def);
            return config.orElse(defaultConfig);
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
        // getOptionalConfiguration already acquires lock
        return getOptional(configuration).orElseThrow(() ->
                new NullPointerException("the specified configuration was not loaded"));
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