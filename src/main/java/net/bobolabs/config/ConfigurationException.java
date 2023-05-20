package net.bobolabs.config;

import org.jetbrains.annotations.NotNull;

public class ConfigurationException extends RuntimeException {

    ConfigurationException(@NotNull String message) {
        super(message);
    }

}
