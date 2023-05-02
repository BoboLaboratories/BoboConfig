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

