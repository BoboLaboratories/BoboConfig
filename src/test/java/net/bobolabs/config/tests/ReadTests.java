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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadTests {

    @TempDir
    static Path directory;

    static Configuration config;

    @BeforeAll
    static void beforeAll() {
        String file = "test_config.yml";
        File configFile = directory.resolve(file).toFile();

        config = ConfigurationBuilder
                .fromFile(configFile)
                .setDefaultResource(file)
                .build();
    }

    @Test
    void contains() {
        assertTrue(config.contains("bytes.values.ok"));
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
        // returns actual value if mapping is present
        assertEquals(Byte.MIN_VALUE, config.getByte("bytes.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getByte("non.existing.key"));

        // throws if mapping is present but cannot be represented as byte
        // assertThrows(ClassCastException.class, () -> config.getByte("bytes.values.cast"));
    }

    @Test
    void getByteDefault() {
        // returns actual value if mapping is present
        assertEquals(Byte.MIN_VALUE, config.getByte("bytes.values.ok", (byte) 1));

        // returns default value if no mapping is present
        assertEquals((byte) -1, config.getByte("non.existing.key", (byte) -1));

        // throws if mapping is present but cannot be represented as byte
        // assertThrows(ClassCastException.class, () -> config.getByte("bytes.values.cast", (byte) 1));
    }

    @Test
    void getByteList() {
        // returns actual value if mapping is present
        assertEquals(List.of((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE), config.getByteList("bytes.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getByteList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getByteList("bytes.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as byte
        assertThrows(ClassCastException.class, () -> config.getByteList("bytes.lists.cast1"));
        assertThrows(ClassCastException.class, () -> config.getByteList("bytes.lists.cast2"));
    }

    @Test
    void getShort() {
        // returns actual value if mapping is present
        assertEquals(Short.MIN_VALUE, config.getShort("shorts.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getShort("non.existing.key"));

        // throws if mapping is present but cannot be represented as short
        // assertThrows(ClassCastException.class, () -> config.getShort("shorts.values.cast"));
    }

    @Test
    void getShortDefault() {
        // returns actual value if mapping is present
        assertEquals(Short.MIN_VALUE, config.getShort("shorts.values.ok", (short) 1));

        // returns default value if no mapping is present
        assertEquals((short) -1, config.getShort("non.existing.key", (short) -1));

        // throws if mapping is present but cannot be represented as short
        // assertThrows(ClassCastException.class, () -> config.getShort("shorts.values.cast", (short) 1));
    }

    @Test
    void getShortList() {
        // returns actual value if mapping is present
        assertEquals(List.of((short) 0, Short.MIN_VALUE, Short.MAX_VALUE), config.getShortList("shorts.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getShortList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getShortList("shorts.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as short
        assertThrows(ClassCastException.class, () -> config.getShortList("shorts.lists.cast"));
    }

    @Test
    void getInt() {
        // returns actual value if mapping is present
        assertEquals(Integer.MIN_VALUE, config.getInt("ints.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getInt("non.existing.key"));

        // throws if mapping is present but cannot be represented as int
        // assertThrows(ClassCastException.class, () -> config.getInt("ints.values.cast"));
    }

    @Test
    void getIntDefault() {
        // returns actual value if mapping is present
        assertEquals(Integer.MIN_VALUE, config.getInt("ints.values.ok", 1));

        // returns default value if no mapping is present
        assertEquals(-1, config.getInt("non.existing.key", -1));

        // throws if mapping is present but cannot be represented as int
        // assertThrows(ClassCastException.class, () -> config.getInt("ints.values.cast", 1));
    }

    @Test
    void getIntList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0, Integer.MIN_VALUE, Integer.MAX_VALUE), config.getIntList("ints.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getIntList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getIntList("ints.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as int
        assertThrows(ClassCastException.class, () -> config.getIntList("ints.lists.cast"));
    }

    @Test
    void getLong() {
        // returns actual value if mapping is present
        assertEquals(-1L, config.getLong("longs.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getLong("non.existing.key"));

        // throws if mapping is present but cannot be represented as long
        // assertThrows(ClassCastException.class, () -> config.getLong("longs.values.cast"));
    }

    @Test
    void getLongDefault() {
        // returns actual value if mapping is present
        assertEquals(-1L, config.getLong("longs.values.ok", 1L));

        // returns default value if no mapping is present
        assertEquals(-1L, config.getLong("non.existing.key", -1L));

        // throws if mapping is present but cannot be represented as long
        // assertThrows(ClassCastException.class, () -> config.getLong("longs.values.cast", 1L));
    }

    @Test
    void getLongList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0L, Long.MIN_VALUE, Long.MAX_VALUE), config.getLongList("longs.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getLongList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getLongList("longs.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as long
        assertThrows(ClassCastException.class, () -> config.getLongList("longs.lists.cast"));
    }

    @Test
    void getFloat() {
        // returns actual value if mapping is present
        assertEquals(Float.MAX_VALUE, config.getFloat("floats.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getFloat("non.existing.key"));

        // throws if mapping is present but cannot be represented as float
        // assertThrows(ClassCastException.class, () -> config.getFloat("floats.values.cast"));
    }

    @Test
    void getFloatDefault() {
        // returns actual value if mapping is present
        assertEquals(Float.MAX_VALUE, config.getFloat("floats.values.ok1", -1f));
        assertEquals(-Float.MAX_VALUE, config.getFloat("floats.values.ok2", -1f));

        // returns default value if no mapping is present
        assertEquals(-1f, config.getFloat("non.existing.key", -1f));

        // throws if mapping is present but cannot be represented as float
        assertThrows(ClassCastException.class, () -> config.getFloat("floats.values.cast", 1f));
    }

    @Test
    void getFloatList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0f, -Float.MAX_VALUE, Float.MAX_VALUE), config.getFloatList("floats.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getFloatList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getFloatList("floats.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as float
        assertThrows(ClassCastException.class, () -> config.getFloatList("floats.lists.cast1"));
        assertThrows(ClassCastException.class, () -> config.getFloatList("floats.lists.cast2"));
    }

    @Test
    void getDouble() {
        // returns actual value if mapping is present
        assertEquals(Double.MAX_VALUE, config.getDouble("doubles.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getDouble("non.existing.key"));

        // throws if mapping is present but cannot be represented as double
        // assertThrows(ClassCastException.class, () -> config.getDouble("doubles.values.cast"));
    }

    @Test
    void getDoubleDefault() {
        // returns actual value if mapping is present
        assertEquals(Double.MAX_VALUE, config.getDouble("doubles.values.ok1", -1D));
        assertEquals(-Double.MAX_VALUE, config.getDouble("doubles.values.ok2", -1D));

        // returns default value if no mapping is present
        assertEquals(-1D, config.getDouble("non.existing.key", -1D));

        // throws if mapping is present but cannot be represented as double
        assertThrows(ClassCastException.class, () -> config.getDouble("doubles.values.cast", 1D));
    }

    @Test
    void getDoubleList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0D, -Double.MAX_VALUE, Double.MAX_VALUE), config.getDoubleList("doubles.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getDoubleList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getDoubleList("doubles.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as double
        assertThrows(ClassCastException.class, () -> config.getDoubleList("doubles.lists.cast"));
    }

    @Test
    void getBoolean() {
        // returns actual value if mapping is present
        assertTrue(config.getBoolean("booleans.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getBoolean("non.existing.key"));

        // throws if mapping is present but cannot be represented as boolean
        assertThrows(ClassCastException.class, () -> config.getBoolean("booleans.values.cast"));
    }

    @Test
    void getBooleanDefault() {
        // returns actual value if mapping is present
        assertTrue(config.getBoolean("booleans.values.ok1", false));
        assertFalse(config.getBoolean("booleans.values.ok2", true));

        // returns default value if no mapping is present
        assertTrue(config.getBoolean("non.existing.key", true));

        // throws if mapping is present but cannot be represented as boolean
        assertThrows(ClassCastException.class, () -> config.getBoolean("booleans.values.cast", true));
    }

    @Test
    void getBooleanList() {
        // returns actual value if mapping is present
        assertEquals(List.of(true, false), config.getBooleanList("booleans.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getBooleanList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getBooleanList("booleans.lists.null"));

        // throws if mapping is present but contains any value that cannot be represented as boolean
        assertThrows(ClassCastException.class, () -> config.getBooleanList("doubles.lists.cast"));
    }

    @Test
    void getString() {
        // returns actual value if mapping is present
        assertEquals("3", config.getString("strings.values.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getString("non.existing.key"));

        // any value can be represented as string
    }

    @Test
    void getStringDefault() {
        // returns actual value if mapping is present
        assertEquals("3", config.getString("strings.values.ok1", "hello"));
        assertEquals("hello", config.getString("strings.values.ok2", "good bye"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getString("non.existing.key"));

        // any value can be represented as string
    }

    @Test
    void getStringList() {
        // returns actual value if mapping is present
        assertEquals(List.of("hello", "Stami", ""), config.getStringList("strings.lists.ok"));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getStringList("non.existing.key"));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getStringList("strings.lists.null"));

        // any value can be represented as string
    }

    @Test
    void getEnum() {
        // returns actual value if mapping is present
        assertEquals(TestEnum.TEST_1, config.getEnum("enums.values.ok", TestEnum.class));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getEnum("non.existing.key", TestEnum.class));

        // throws if mapping is present but cannot be represented as the given enum constant
        assertThrows(IllegalArgumentException.class, () -> config.getEnum("enums.values.cast", TestEnum.class));
    }

    @Test
    void getEnumDefault() {
        // returns actual value if mapping is present
        assertEquals(TestEnum.TEST_1, config.getEnum("enums.values.ok", TestEnum.class, TestEnum.TEST_2));

        // returns default value if no mapping is present
        assertEquals(TestEnum.TEST_2, config.getEnum("non.existing.key", TestEnum.class, TestEnum.TEST_2));

        // throws if mapping is present but cannot be represented as the given enum constant
        assertThrows(IllegalArgumentException.class, () -> config.getEnum("enums.values.cast", TestEnum.class, TestEnum.TEST_1));
    }

    @Test
    void getEnumList() {
        // returns actual value if mapping is present
        assertEquals(List.of(TestEnum.TEST_1, TestEnum.TEST_2), config.getEnumList("enums.lists.ok", TestEnum.class));

        // throws if no mapping is present
        assertThrows(NullPointerException.class, () -> config.getEnumList("non.existing.key", TestEnum.class));

        // throws if any of the mapped values is null
        assertThrows(NullPointerException.class, () -> config.getEnumList("enums.lists.null", TestEnum.class));

        // throws if mapping is present but contains any value that cannot be represented as the given enum constant
        assertThrows(IllegalArgumentException.class, () -> config.getEnumList("enums.lists.cast", TestEnum.class));
    }

}
