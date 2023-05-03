package net.bobolabs.config;

import net.bobolabs.config.annotation.Config;
import net.bobolabs.utils.Reloadable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.UnaryOperator;


// A Glowy piace :D
public final class ConfigManager<T extends Enum<T> & Configurable> implements Reloadable {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<T, Configuration> configurations;
    private final UnaryOperator<String> normalizer;
    private final Class<T> clazz;
    private final File dataFolder;

    public ConfigManager(@NotNull File dataFolder, @NotNull Class<T> clazz) {
        this(clazz, dataFolder, String::toLowerCase);
    }

    public ConfigManager(@NotNull Class<T> clazz,
                         @NotNull File dataFolder,
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
                if (annotation != null) {
                    String path = annotation.path();
                    Configuration config = ConfigurationBuilder
                            .fromFile(dataFolder, path.isEmpty() ? normalizer.apply(path) : path)
                            .saveDefaultFromResource(annotation.defaultResource())
                            .autoSave(annotation.autoSave())
                            .build();

                    T key = Enum.valueOf(clazz, field.getName());
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

    public @NotNull Configuration getConfig(@NotNull T config) {
        lock.readLock().lock();
        try {
            return configurations.get(config);
        } finally {
            lock.readLock().unlock();
        }
    }
}