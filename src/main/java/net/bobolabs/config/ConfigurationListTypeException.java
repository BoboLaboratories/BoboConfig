package net.bobolabs.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurationListTypeException extends ConfigurationException {

    ConfigurationListTypeException(@NotNull String path,
                                   @NotNull Class<?> requestedType,
                                   @NotNull List<?> list,
                                   @Nullable Object value) {
        super(prepareMessage(path, requestedType, list, value));
    }

    private static @NotNull String prepareMessage(@NotNull String path,
                                                  @NotNull Class<?> requestedType,
                                                  @NotNull List<?> list,
                                                  @Nullable Object value) {
        StringBuilder builder = new StringBuilder();

        builder.append("list `")
                .append(list)
                .append("` found in path `")
                .append(path)
                .append("` could not be converted to ")
                .append(List.class)
                .append("<")
                .append(requestedType)
                .append("> because it contains ")
        ;

        if (value == null) {
            builder.append("null element(s)");
        } else {
            builder.append("value `")
                    .append(value)
                    .append("` of type ")
                    .append(value.getClass())
                    .append(" that cannot be converted to ")
                    .append(requestedType);
        }

        return builder.toString();
    }

}
