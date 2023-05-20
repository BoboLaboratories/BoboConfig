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

package net.bobolabs.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoSaveTests {

    static final String FILE_NAME = "write_test_config.yml";

    @TempDir
    static Path directory;

    static Configuration config;

    @Test
    void autoSaveWorks() throws IOException, URISyntaxException {
        config = ConfigurationBuilder
                .fromFile(directory.toFile(), FILE_NAME)
                .setDefaultResource(FILE_NAME)
                .autoSave(true)
                .build();

        config.set("values.byte", (byte) 1);
        config.set("values.short", (short) 2);
        config.set("values.int", 3);
        config.set("values.long", 4);
        config.set("values.float", 5.0);
        config.set("values.double", 6.0);
        config.set("values.boolean", true);
        config.set("values.string", "hello");
        config.set("values.enum", TestEnum.TEST_1);

        // we do not call .save() manually

        URL resourceUrl = getClass().getClassLoader().getResource("expected_" + FILE_NAME);
        String expected = Files.readString(Paths.get(Objects.requireNonNull(resourceUrl).toURI()));
        String saved = Files.readString(directory.resolve(FILE_NAME));

        assertEquals(expected, saved);
    }

}
