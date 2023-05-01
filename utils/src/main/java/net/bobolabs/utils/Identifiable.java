package net.bobolabs.utils;

import org.jetbrains.annotations.NotNull;

public interface Identifiable<I> {

    @NotNull I getId();

}