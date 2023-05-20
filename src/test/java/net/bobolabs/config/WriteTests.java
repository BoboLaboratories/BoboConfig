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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class WriteTests {

    static final String FILE_NAME = "write_test_config.yml";

    @TempDir
    static Path directory;

    static Configuration config;

    @BeforeEach
    void beforeEach() {
        config = ConfigurationBuilder
                .fromFile(directory.toFile(), FILE_NAME)
                .setDefaultResource(FILE_NAME)
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
    }

    @Test
    void changesAreWrittenInMemory() {
        assertEquals((byte) 1, config.getByte("values.byte"));
        assertEquals((short) 2, config.getShort("values.short"));
        assertEquals(3, config.getInt("values.int"));
        assertEquals(4, config.getLong("values.long"));
        assertEquals(5.0, config.getFloat("values.float"));
        assertEquals(6.0, config.getDouble("values.double"));
        assertTrue(config.getBoolean("values.boolean"));
        assertEquals("hello", config.getString("values.string"));
        assertEquals(TestEnum.TEST_1, config.getEnum("values.enum", TestEnum.class));
    }

    @Test
    void changesAreWrittenOnDisk() throws IOException, URISyntaxException {
        config.save();
        URL resourceUrl = getClass().getClassLoader().getResource("expected_" + FILE_NAME);
        String expected = Files.readString(Paths.get(Objects.requireNonNull(resourceUrl).toURI()));
        String saved = Files.readString(directory.resolve(FILE_NAME));
        assertEquals(expected, saved);
    }

    @Test
    void reloadDoesIndeedReload_1() {
        assertEquals((byte) 1, config.getByte("values.byte"));
        assertEquals((short) 2, config.getShort("values.short"));
        assertEquals(3, config.getInt("values.int"));
        assertEquals(4, config.getLong("values.long"));
        assertEquals(5.0, config.getFloat("values.float"));
        assertEquals(6.0, config.getDouble("values.double"));
        assertTrue(config.getBoolean("values.boolean"));
        assertEquals("hello", config.getString("values.string"));
        assertEquals(TestEnum.TEST_1, config.getEnum("values.enum", TestEnum.class));

        config.reload();

        // there should be no keys left because changes were not written on
        // disk therefore the file that was reloaded was supposed to be empty
        assertEquals(Collections.emptySet(), config.getKeys(TraversalMode.BRANCHES));
    }

    @Test
    void reloadDoesIndeedReload_2() throws IOException {
        assertEquals((byte) 1, config.getByte("values.byte"));
        assertEquals((short) 2, config.getShort("values.short"));
        assertEquals(3, config.getInt("values.int"));
        assertEquals(4, config.getLong("values.long"));
        assertEquals(5.0, config.getFloat("values.float"));
        assertEquals(6.0, config.getDouble("values.double"));
        assertTrue(config.getBoolean("values.boolean"));
        assertEquals("hello", config.getString("values.string"));
        assertEquals(TestEnum.TEST_1, config.getEnum("values.enum", TestEnum.class));

        try (InputStream in = this.getClass().getResourceAsStream("/swap_write_config.yml")) {
            Files.copy(Objects.requireNonNull(in), directory.resolve(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
        }

        config.reload();

        // checked values differ from before
        assertEquals((byte) 2, config.getByte("values.byte"));
        assertEquals((short) 3, config.getShort("values.short"));
        assertEquals(4, config.getInt("values.int"));
        assertEquals(5, config.getLong("values.long"));
        assertEquals(6.0, config.getFloat("values.float"));
        assertEquals(7.0, config.getDouble("values.double"));
        assertFalse(config.getBoolean("values.boolean"));
        assertEquals("good bye", config.getString("values.string"));
        assertEquals(TestEnum.TEST_2, config.getEnum("values.enum", TestEnum.class));
    }

    @Test
    void setNullRemovesValue() {
        config.set("values.int", 10); // anything that we're sure is not 0
        config.set("values.int", null);
        assertEquals(0, config.getInt("values.int"));
    }

    @Test
    void setNullRemovesSection() {
        config.set("section.s1", null);
        assertThrows(NullPointerException.class, () -> config.getSection("section.s1"));
    }

    @Test
    void setNullValueRemovesFromFile() {
        config.set("values.int", null);
        config.save();
        config.reload();
        assertEquals(0, config.getInt("values.int"));
    }

    @Test
    void setNullSectionRemovesFromFile() {
        config.set("values", null);
        config.save();
        config.reload();
        assertEquals(Collections.emptySet(), config.getKeys(TraversalMode.BRANCHES));
    }

    @Test
    void unsetValueRemovesValue() {
        config.unset("values.int");
        assertEquals(0, config.getInt("values.int"));
    }

    @Test
    void unsetRemovesSection() {
        config.unset("section.s1");
        assertThrows(NullPointerException.class, () -> config.getSection("section.s1"));
    }

    @Test
    void unsetValueRemovesFromFile() {
        config.unset("values.int");
        config.save();
        config.reload();
        assertEquals(0, config.getInt("values.int"));
    }

    @Test
    void unsetSectionRemovesFromFile() {
        config.unset("values");
        config.save();
        config.reload();
        assertEquals(Collections.emptySet(), config.getKeys(TraversalMode.BRANCHES));
    }

    @Test
    void getOrSet() {
        Object object = new Object(); // any object
        assertFalse(config.contains("non.existing.key"));
        //assertSame(object, config.getOrSet("non.existing.key", object));
        assertTrue(config.contains("non.existing.key"));
        assertSame(object, config.get("non.existing.key"));
    }

}
