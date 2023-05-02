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

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractRegistry<I, T extends Identifiable<I>> implements Registry<I, T> {

    private final Map<I, T> entries;

    protected AbstractRegistry() {
        entries = getDataStructure();
    }

    @Override
    public boolean contains(@NotNull T entry) {
        return contains(entry.getId());
    }

    @Override
    public boolean contains(@NotNull I id) {
        id = normalizeId(id);
        return entries.containsKey(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <U extends T> U getById(@NotNull I id) {
        id = normalizeId(id);
        return (U) entries.get(id);
    }

    @Override
    @Contract("-> new")
    public @NotNull Collection<T> getAll() {
        return new HashSet<>(entries.values());
    }

    @Override
    @Contract("-> new")
    public @NotNull Collection<I> getAllIds() {
        return new HashSet<>(entries.keySet());
    }

    @Override
    public <U extends T> @Nullable U getFirstByPredicate(@NotNull Predicate<U> predicate) {
        // TODO:
        return null;
    }

    @Override
    @Contract("_ -> new")
    @SuppressWarnings("unchecked")
    public @NotNull <U extends T> Collection<U> getAllByPredicate(@NotNull Predicate<U> predicate) {
        Predicate<U> negated = predicate.negate();
        Collection<T> all = getAll();
        all.removeIf(entry -> {
            try {
                return negated.test((U) entry);
            } catch (ClassCastException ignored) {
                // entry has different type
                return false;
            }
        });
        return (Collection<U>) all;
    }

    @Override
    public void register(@NotNull T entry) {
        // TODO: Fix here! :D
//        checkArgument(!contains(entry), "%s is already registered", entry.getId());
        I id = normalizeId(entry.getId());
        entries.put(id, entry);
    }

    @Override
    public void registerAll(@NotNull Collection<T> entries) {
        for (T entry : entries) {
            register(entry);
        }
    }

    @Override
    public void unregister(@NotNull T entry) {
        unregister(entry.getId());
    }

    @Override
    public @Nullable T unregister(@NotNull I id) {
        // TODO: Fix here! :D
//        checkArgument(contains(id), "%s is not registered", id);
        id = normalizeId(id);
        return entries.remove(id);
    }

    @Override
    public void unregisterAll(@NotNull Collection<T> entries) {
        for (T entry : entries) {
            unregister(entry);
        }
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return getAll().iterator();
    }

    protected @NotNull Map<I, T> getDataStructure() {
        return new HashMap<>();
    }

    protected void clear() {
        entries.clear();
    }

    protected @NotNull I normalizeId(@NotNull I id) {
        return id;
    }

}