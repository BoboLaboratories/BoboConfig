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
import net.bobolabs.config.Keys;
import net.md_5.bungee.config.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadTests {

    @TempDir
    static Path directory;

    static Configuration config;
    static net.md_5.bungee.config.Configuration md5;

    @BeforeAll
    static void beforeAll() throws IOException {
        String file = "config.yml";
        File configFile = directory.resolve(file).toFile();

        config = ConfigurationBuilder
                .fromFile(configFile)
                .setDefaultResource(file)
                .build();

        md5 = net.md_5.bungee.config.ConfigurationProvider
                .getProvider(YamlConfiguration.class)
                .load(configFile);
    }

    @Test
    void contains() {
        // ours
        assertTrue(config.contains("values.byte"));
        assertFalse(config.contains("non.existing.key"));

        // behaves like md_5
        assertEquals(config.contains("values.byte"), md5.contains("values.byte"));
        assertEquals(config.contains("non.existing.key"), md5.contains("non.existing.key"));
    }

    @Test
    void getWithoutDefault() {
        // ours
        assertEquals(1, config.get("values.byte"));
        assertNull(config.get("non.existing.key"));

        // behaves like md_5
        assertEquals(md5.get("values.byte"), config.get("values.byte"));
        assertEquals(md5.get("non.existing.key"), config.get("non.existing.key"));
    }

    @Test
    void getWithDefault() {
        Object def = new Object(); // any object

        // ours
        assertEquals(config.get("non.existing.key", def), def);
        assertNotEquals(config.get("values.byte", def), def);

        // behaves like md_5
        assertSame(md5.get("non.existing.key", def), config.get("non.existing.key", def));
        assertSame(md5.get("values.byte", def), config.get("values.byte", def));
    }

    // TODO set

    @Test
    void getSection() { // TODO
        // ours
        assertNull(config.getSection("non.existing.key"));

        // behaves like md_5 -- NO, we do not silently create the section.
        // assertNull(md5.getSection("non.existing.key"));
    }

    // TODO getSection with default

    @Test
    void geKeys() {
        System.out.println("ROOT:");
        for (String key : config.getKeys(Keys.ROOT)) {
            System.out.println(key);
        }
        System.out.println();
        System.out.println("Leaves:");
        for (String key : config.getKeys(Keys.LEAVES)) {
            System.out.println(key);
        }
        System.out.println();
        System.out.println("Branches:");
        for (String key : config.getKeys(Keys.BRANCHES)) {
            System.out.println(key);
        }
    }

    @Test
    void getByte() {
        // ours
        assertEquals((byte) 1, config.getByte("values.byte"));
        assertEquals((byte) 0, config.getByte("non.existing.key"));
        assertEquals((byte) -1, config.getByte("non.existing.key", (byte) -1));

        // behaves like md_5
        assertEquals(md5.getByte("values.byte"), config.getByte("values.byte"));
        assertEquals(md5.getByte("non.existing.key"), config.getByte("non.existing.key"));
        assertEquals(md5.getByte("non.existing.key", (byte) -1), config.getByte("non.existing.key", (byte) -1));
    }

    @Test
    void getByteList() {
        // ours
        assertEquals(List.of((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 1000), config.getByteList("lists.bytes"));
    }

    @Test
    void getShort() {
        // ours
        assertEquals((short) 2, config.getShort("values.short"));
        assertEquals((short) 0, config.getShort("non.existing.key"));
        assertEquals((short) -1, config.getShort("non.existing.key", (short) -1));

        // behaves like md_5
        assertEquals(md5.getShort("values.short"), config.getShort("values.short"));
        assertEquals(md5.getShort("non.existing.key"), config.getShort("non.existing.key"));
        assertEquals(md5.getShort("non.existing.key", (short) -1), config.getShort("non.existing.key", (short) -1));
    }

    // TODO short list

    @Test
    void getInt() {
        // ours
        assertEquals(3, config.getInt("values.int"));
        assertEquals(0, config.getInt("non.existing.key"));
        assertEquals(-1, config.getInt("non.existing.key", -1));

        // behaves like md_5
        assertEquals(md5.getInt("values.int"), config.getInt("values.int"));
        assertEquals(md5.getInt("non.existing.key"), config.getInt("non.existing.key"));
        assertEquals(md5.getInt("non.existing.key", -1), config.getInt("non.existing.key", -1));
    }

    // TODO int list

    @Test
    void getLong() {
        // ours
        assertEquals(4L, config.getLong("values.long"));
        assertEquals(0L, config.getLong("non.existing.key"));
        assertEquals(-1L, config.getLong("non.existing.key", -1L));

        // behaves like md_5
        assertEquals(md5.getLong("values.long"), config.getLong("values.long"));
        assertEquals(md5.getLong("non.existing.key"), config.getLong("non.existing.key"));
        assertEquals(md5.getLong("non.existing.key", -1L), config.getLong("non.existing.key", -1L));
    }

    // TODO long list

    @Test
    void getFloat() {
        // ours
        assertEquals(5.1f, config.getFloat("values.float"));
        assertEquals(0f, config.getFloat("non.existing.key"));
        assertEquals(-1f, config.getFloat("non.existing.key", -1f));

        // behaves like md_5
        assertEquals(md5.getFloat("values.float"), config.getFloat("values.float"));
        assertEquals(md5.getFloat("non.existing.key"), config.getFloat("non.existing.key"));
        assertEquals(md5.getFloat("non.existing.key", -1f), config.getFloat("non.existing.key", -1f));
    }

    // TODO float list

    @Test
    void getDouble() {
        // ours
        assertEquals(6.2D, config.getDouble("values.double"));
        assertEquals(0D, config.getDouble("non.existing.key"));
        assertEquals(-1D, config.getDouble("non.existing.key", -1D));

        // behaves like md_5
        assertEquals(md5.getDouble("values.double"), config.getDouble("values.double"));
        assertEquals(md5.getDouble("non.existing.key"), config.getDouble("non.existing.key"));
        assertEquals(md5.getDouble("non.existing.key", -1D), config.getDouble("non.existing.key", -1D));
    }

    // TODO double list

    @Test
    void getBoolean() {
        // ours
        assertTrue(config.getBoolean("values.boolean"));
        assertFalse(config.getBoolean("non.existing.key"));
        assertTrue(config.getBoolean("non.existing.key", true));

        // behaves like md_5
        assertEquals(md5.getBoolean("values.boolean"), config.getBoolean("values.boolean"));
        assertEquals(md5.getBoolean("non.existing.key"), config.getBoolean("non.existing.key"));
        assertEquals(md5.getBoolean("non.existing.key", true), config.getBoolean("non.existing.key", true));
    }

    @Test
    void getBooleanList() {
        assertEquals(List.of(true, false), config.getBooleanList("lists.booleans.1"));
        assertEquals(List.of(true, false), config.getBooleanList("lists.booleans.2"));
        assertEquals(Collections.emptyList(), config.getBooleanList("lists.booleans.3"));
    }

    @Test
    void getString() {
        // ours
        assertEquals("sette :D", config.getString("values.string"));
        assertNull(config.getString("non.existing.key"));
        assertEquals("weeeee", config.getString("non.existing.key", "weeeee"));

        // behaves like md_5, (*) except we return null instead of "" when the key does not exist and no default is provided
        assertEquals(md5.getString("values.string"), config.getString("values.string"));
        // (*) assertEquals(md5.getString("non.existing.key"), config.getString("non.existing.key"));
        assertEquals(md5.getString("non.existing.key", "weeeee"), config.getString("non.existing.key", "weeeee"));
    }

    @Test
    void getStringList() {
        assertEquals(List.of("1", "c", ""), config.getStringList("values.list1"));
    }

    @Test
    void getEnum() {
        // ours
        assertEquals(TestEnum.TEST1, config.getEnum("values.enum", TestEnum.class));
        assertNull(config.getEnum("non.existing.key", TestEnum.class));
        assertEquals(TestEnum.TEST2, config.getEnum("non.existing.key", TestEnum.class, TestEnum.TEST2));

        // md_5 does not support enums
    }

    @Test
    void getEnumList() {
        assertEquals(List.of(TestEnum.TEST1, TestEnum.TEST2), config.getEnumList("lists.enums", TestEnum.class));

        // md_5 does not support enums
    }

}
