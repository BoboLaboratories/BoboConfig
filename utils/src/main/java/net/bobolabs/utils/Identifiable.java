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

import org.jetbrains.annotations.NotNull;

public interface Identifiable<I> {

    @NotNull I getId();

}