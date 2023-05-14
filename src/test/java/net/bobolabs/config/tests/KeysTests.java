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

package net.bobolabs.config.tests;

import net.bobolabs.config.Configuration;
import net.bobolabs.config.ConfigurationBuilder;
import net.bobolabs.config.KeyResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeysTests {

    @TempDir
    static Path directory;

    static Configuration config;

    @BeforeAll
    static void beforeAll() throws IOException {
        String file = "keys.yml";
        File configFile = directory.resolve(file).toFile();

        config = ConfigurationBuilder
                .fromFile(configFile)
                .setDefaultResource(file)
                .build();
    }

    @Test
    void test() {
        assertEquals(Set.of("a", "g", "i"), config.getKeys(KeyResolver.ROOT));
        assertEquals(Set.of("a", "a.b", "a.b.c", "a.b.d", "a.e", "a.e.f", "g", "g.h", "i"), config.getKeys(KeyResolver.BRANCHES));
        assertEquals(Set.of("a.b.c", "a.b.d", "a.e.f", "g.h", "i"), config.getKeys(KeyResolver.LEAVES));
    }

    // TODO test on subSection

}
