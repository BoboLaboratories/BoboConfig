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
import net.bobolabs.config.ConfigurationSection;
import net.bobolabs.config.TraversalMode;
import net.md_5.bungee.config.YamlConfiguration;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReadTests {

    @TempDir
    static Path directory;

    static Configuration config;
    static net.md_5.bungee.config.Configuration md5;

    @BeforeAll
    static void beforeAll() throws IOException {
        String file = "test_config.yml";
        File configFile = directory.resolve(file).toFile();

        /*
            config.get<type>("existing.key")            | when correct type     -> OK (returns value)
            config.get<type>("existing.key")            | when incorrect type   -> ConfigurationException (o qualunque cosa che esprime correttamente il wrong type)
            config.get<type>("non.existing.key")        | unconditional         -> NullPointer
            config.get<type>("non.existing.key", def)   | unconditional         -> OK (returns def)
         */

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
        assertTrue(config.contains("values.byte"));
        assertFalse(config.contains("non.existing.key"));
    }

    @Test
    void get() {
        assertEquals(3, config.get("values.int"));
        assertThrows(NullPointerException.class, () -> config.get("non.existing.key"));
    }

    @Test
    void getWithDefault() {
        Object def = new Object(); // any object
        assertEquals(3, config.get("values.int", 0));
        assertEquals(def, config.get("non.existing.key", def));
        assertNull(config.get("non.existing.key", null));
    }

    // TODO getOrSet ------- ???

    @Test
    void getList() {
        List<Object> list = new ArrayList<>();
        list.add(0);
        list.add("A");
        list.add(null);
        list.add("");
        list.add(List.of(2, 1));
        list.add(null);
        assertEquals(list, config.getList("lists.objects"));

        // test original list is not modified (@Contract("_ -> new"))
        config.getList("lists.objects").add("O");
        assertEquals(list, config.getList("lists.objects"));
    }

    @Test
    void getSection() {
        assertNotNull(config.getSection("section"));
        assertThrows(ClassCastException.class, () -> config.getSection("values.int"));
        assertThrows(NullPointerException.class, () -> config.getSection("non.existing.key"));

        // ours
//        assertNotNull(config.getOptionalSection("section"));
//        assertNull(config.getOptionalSection("non.existing.key"));
//
//        ConfigurationSection section1 = config.getSection("section");
//        ConfigurationSection subSection2 = section1.getSection("s2");
//        assertEquals(Set.of("s1.s1_s1.path1", "s1.s1_s1.path2", "s2.path3", "s2.path4"), section1.getKeys(TraversalMode.LEAVES));
//        assertEquals(Set.of("path3", "path4"), subSection2.getKeys(TraversalMode.LEAVES));
//
//        ClassCastException e = assertThrows(ClassCastException.class, () -> subSection2.getSection("path3"));
//        assertThat(e).hasMessageThat().contains("class java.lang.Integer cannot be cast to class " + ConfigurationSection.class.getCanonicalName());
//        assertNull(subSection2.getOptionalSection("path5"));

        // behaves like md_5 -- NO, we do not silently create the section.
    }

    @Test
    void getSectionsWithDefault() {
        ConfigurationSection def = config.getSection("section.s2");
        ConfigurationSection section = config.getSection("non.existing.key", def);
        assertSame(def, section);
    }

    @Test
    void getByte() {
        assertEquals((byte) 1, config.getByte("values.byte"));
        assertThrows(ClassCastException.class, () -> config.getByte("values.string"));
        assertThrows(NullPointerException.class, () -> config.getByte("non.existing.key"));
        assertEquals((byte) -1, config.getByte("non.existing.key", (byte) -1));
    }

    @Test
    void getByteList() {
        assertEquals(List.of((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE), config.getByteList("lists.bytes.ok"));
        assertThrows(NullPointerException.class, () -> config.getByteList("lists.bytes.null-pointer"));
        assertThrows(ClassCastException.class, () -> config.getByteList("lists.bytes.class-cast"));
    }

    @Test
    void getShort() {
        assertEquals((short) 2, config.getShort("values.short"));
        assertThrows(ClassCastException.class, () -> config.getByte("values.string"));
        assertThrows(NullPointerException.class, () -> config.getByte("non.existing.key"));
        assertEquals((short) -1, config.getShort("non.existing.key", (short) -1));
    }

    @Test
    void getShortList() {
        assertEquals(List.of((short) 0, Short.MIN_VALUE, Short.MAX_VALUE), config.getShortList("lists.shorts"));
    }

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

    @Test
    void getIntList() {
        assertEquals(List.of(0, Integer.MIN_VALUE, Integer.MAX_VALUE), config.getIntList("lists.ints"));
    }

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

    @Test
    void getLongList() {
        assertEquals(List.of(0L, Long.MIN_VALUE, Long.MAX_VALUE), config.getLongList("lists.longs"));
    }

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

    @Test
    void getFloatList() {
        assertEquals(List.of(0f, Float.MIN_VALUE, Float.MAX_VALUE), config.getFloatList("lists.floats"));
    }

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

    @Test
    void getDoubleList() {
        assertEquals(List.of(0D, Double.MIN_VALUE, Double.MAX_VALUE), config.getDoubleList("lists.doubles"));
    }

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

        // behaves like md_5
        // (*) except we return null instead of "" when the key does not exist and no default value is provided
        // (*) assertEquals(md5.getString("non.existing.key"), config.getString("non.existing.key"));
        assertEquals(md5.getString("values.string"), config.getString("values.string"));
        assertEquals(md5.getString("non.existing.key", "weeeee"), config.getString("non.existing.key", "weeeee"));
    }

    @Test
    void getStringList() {
        assertEquals(List.of("1", "c", "", "A", "5.0"), config.getStringList("lists.strings"));
    }

    @Test
    void getEnum() {
        // ours
        assertEquals(TestEnum.TEST_1, config.getEnum("values.enum", TestEnum.class));
        assertNull(config.getEnum("non.existing.key", TestEnum.class));
        assertEquals(TestEnum.TEST_2, config.getEnum("non.existing.key", TestEnum.class, TestEnum.TEST_2));

        // md_5 does not support enums
    }

    @Test
    void getEnumList() {
        assertEquals(List.of(TestEnum.TEST_1, TestEnum.TEST_2), config.getEnumList("lists.enums", TestEnum.class));

        // md_5 does not support enums
    }

}
