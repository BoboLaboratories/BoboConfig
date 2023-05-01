package net.bobolabs.utils;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

public interface Registry<I, T extends Identifiable<I>> extends Iterable<T> {

    boolean contains(@NotNull T entry);

    boolean contains(@NotNull I id);

    @Nullable <U extends T> U getById(@NotNull I id);

    @Contract("-> new")
    @NotNull Collection<T> getAll();

    @Contract("-> new")
    @NotNull Collection<I> getAllIds();

    @Nullable <U extends T> U getFirstByPredicate(@NotNull Predicate<U> predicate);

    @Contract("_ -> new")
    @NotNull <U extends T> Collection<U> getAllByPredicate(@NotNull Predicate<U> predicate);

    void register(@NotNull T entry);

    void registerAll(@NotNull Collection<T> entries);

    void unregister(@NotNull T entry);

    @Nullable T unregister(@NotNull I id);

    void unregisterAll(@NotNull Collection<T> entries);

}

