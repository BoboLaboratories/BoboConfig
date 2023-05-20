package net.bobolabs.config;

import org.jetbrains.annotations.NotNull;

public class ConfigurationTypeException extends ClassCastException {

    ConfigurationTypeException(@NotNull String path, @NotNull Object value, @NotNull Class<?> requestedType) {
        super(
                "value `" + value + "` of type " + value.getClass()
                + " found in path `" + path + "` cannot be converted to " + requestedType);
    }

}
