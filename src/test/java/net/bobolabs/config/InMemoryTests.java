/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023-2024 BoboLabs.net
 * Copyright (C) 2023-2024 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023-2024 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023-2024 Third party contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bobolabs.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static com.google.common.truth.Truth.assertThat;
import static net.bobolabs.config.TestUtils.listOf;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTests {

    static final String EMPTY_FILE = "empty_config.yml";

    @TempDir
    Path directory;

    Configuration config;


    @BeforeEach
    void beforeEach() {
        File file = directory.resolve(EMPTY_FILE).toFile();
        config = ConfigurationLoader
                .fromFile(file)
                .setDefaultResource(EMPTY_FILE)
                .load();

        // bytes
        ConfigurationSection byteValues = config.createSection("bytes.values");
        byteValues.set("ok", Byte.MIN_VALUE);
        byteValues.set("cast", true);
        ConfigurationSection byteLists = config.createSection("bytes.lists");
        byteLists.set("ok", listOf((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE));
        byteLists.set("null", listOf((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE, null));
        byteLists.set("cast", listOf((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE, new ArrayList<>()));

        // shorts
        ConfigurationSection shortValues = config.createSection("shorts.values");
        shortValues.set("ok", Short.MIN_VALUE);
        shortValues.set("cast", "");
        ConfigurationSection shortLists = config.createSection("shorts.lists");
        shortLists.set("ok", listOf((short) 0, Short.MIN_VALUE, Short.MAX_VALUE));
        shortLists.set("null", listOf((short) 0, Short.MIN_VALUE, Short.MAX_VALUE, null));
        shortLists.set("cast", listOf((short) 0, Short.MIN_VALUE, Short.MAX_VALUE, new ArrayList<>()));

        // integers
        ConfigurationSection intValues = config.createSection("ints.values");
        intValues.set("ok", Integer.MIN_VALUE);
        intValues.set("cast", new ArrayList<>());
        ConfigurationSection intLists = config.createSection("ints.lists");
        intLists.set("ok", listOf(0, Integer.MIN_VALUE, Integer.MAX_VALUE));
        intLists.set("null", listOf(0, Integer.MIN_VALUE, Integer.MAX_VALUE, null));
        intLists.set("cast", listOf(0, Integer.MIN_VALUE, Integer.MAX_VALUE, new ArrayList<>()));

        // longs
        ConfigurationSection longValues = config.createSection("longs.values");
        longValues.set("ok", Long.MIN_VALUE);
        longValues.set("cast", new HashMap<>());
        ConfigurationSection longLists = config.createSection("longs.lists");
        longLists.set("ok", listOf((long) 0, Long.MIN_VALUE, Long.MAX_VALUE));
        longLists.set("null", listOf((long) 0, Long.MIN_VALUE, Long.MAX_VALUE, null));
        longLists.set("cast", listOf((long) 0, Long.MIN_VALUE, Long.MAX_VALUE, ""));

        // floats
        ConfigurationSection floatsValues = config.createSection("floats.values");
        floatsValues.set("ok", -Float.MAX_VALUE);
        floatsValues.set("cast", "weeeee :D");
        ConfigurationSection floatsLists = config.createSection("floats.lists");
        floatsLists.set("ok", listOf(0.0f, -Float.MAX_VALUE, Float.MAX_VALUE));
        floatsLists.set("null", listOf(0.0f, -Float.MAX_VALUE, Float.MAX_VALUE, null));
        floatsLists.set("cast", listOf(0.0f, -Float.MAX_VALUE, Float.MAX_VALUE, "hello"));

        // doubles
        ConfigurationSection doubleValues = config.createSection("doubles.values");
        doubleValues.set("ok", -Double.MAX_VALUE);
        doubleValues.set("cast", new ArrayList<>());
        ConfigurationSection doubleLists = config.createSection("doubles.lists");
        doubleLists.set("ok", listOf(0.0D, -Double.MAX_VALUE, Double.MAX_VALUE));
        doubleLists.set("null", listOf(0.0D, -Double.MAX_VALUE, Double.MAX_VALUE, null));
        doubleLists.set("cast", listOf(0.0D, -Double.MAX_VALUE, Double.MAX_VALUE, listOf("another_list")));

        // booleans
        ConfigurationSection booleanValues = config.createSection("booleans.values");
        booleanValues.set("ok", true);
        booleanValues.set("cast", 34);
        ConfigurationSection booleanLists = config.createSection("booleans.lists");
        booleanLists.set("ok", listOf(true, false));
        booleanLists.set("null", listOf(true, false, null));
        booleanLists.set("cast", listOf(true, false, -0.6f));

        // strings
        ConfigurationSection stringValues = config.createSection("strings.values");
        stringValues.set("ok", 3);
        ConfigurationSection stringLists = config.createSection("strings.lists");
        stringLists.set("ok", listOf("hello", "Stami", ""));
        stringLists.set("null", listOf("hello", "Stami", "", null));

        // enums
        ConfigurationSection enumValues = config.createSection("enums.values");
        enumValues.set("ok", TestEnum.TEST_1);
        enumValues.set("cast", "TEST_3");
        ConfigurationSection enumLists = config.createSection("enums.lists");
        enumLists.set("ok", listOf(TestEnum.TEST_1, TestEnum.TEST_2));
        enumLists.set("null", listOf(TestEnum.TEST_1, TestEnum.TEST_2, null));
        enumLists.set("cast", listOf(TestEnum.TEST_1, TestEnum.TEST_2, 3));

        // objects
        ConfigurationSection objects = config.createSection("objects");
        objects.set("list", listOf(0, "A", null, "", true, List.of(2, 1), null, new ArrayList<>(), new HashMap<>()));

        // keys
        ConfigurationSection keySection = config.createSection("keys");
        keySection.set("i", 1);
        ConfigurationSection sectionA = keySection.createSection("a");
        ConfigurationSection sectionB = sectionA.createSection("b");
        sectionB.set("c", 2);
        sectionB.set("d", 3);
        ConfigurationSection sectionE = sectionA.createSection("e");
        sectionE.set("f", 4);
        ConfigurationSection sectionG = keySection.createSection("g");
        sectionG.set("h", 5);
    }

    @AfterEach
    void afterEach() {
        config = null;
    }

    @Test
    void contains() {
        // true
        assertTrue(config.contains("bytes.values.ok"));
        assertNotNull(config.get("bytes.values.ok"));

        // false
        assertFalse(config.contains("non.existing.key"));
        NullPointerException e = assertThrows(NullPointerException.class, () -> config.getList("non.existing.key"));
        assertThat(e).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");
    }

    @Test
    void get() {
        // returns actual value if mapping is present
        assertEquals(Integer.MIN_VALUE, config.get("ints.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException e = assertThrows(NullPointerException.class, () -> config.get("non.existing.key"));
        assertThat(e).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");
    }

    @Test
    void getDefault() {
        // any object
        Object def = new Object();

        // returns actual value if mapping is present
        assertEquals(Integer.MIN_VALUE, config.get("ints.values.ok", def));

        // returns default value if mapping is missing
        assertNull(config.get("non.existing.key", null));
        assertSame(def, config.get("non.existing.key", def));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));
    }

    @Test
    void getList() {
        // returns actual value if mapping is present
        List<Object> list = listOf(0, "A", null, "", true, List.of(2, 1), null, new ArrayList<>(), new HashMap<>());
        assertEquals(list, config.getList("objects.list"));

        // test @Contract("_ -> new")
        config.getList("objects.list").add("anything");
        assertEquals(list, config.getList("objects.list"));

        // throws NullPointerException if mapping is missing
        NullPointerException e = assertThrows(NullPointerException.class, () -> config.getList("non.existing.key"));
        assertThat(e).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");
    }

    @Test
    void set() {
        // set value
        config.set("ints.values.ok", 3);

        // changes are written in memory
        assertEquals(3, config.get("ints.values.ok"));

        // set null values
        config.set("ints.values.ok", null);
        assertFalse(config.contains("ints.values.ok"));

        // set null section
        config.set("bytes.values", null);
        assertFalse(config.contains("bytes.values"));
        assertNull(config.getSection("bytes.values", null));

        // throws NullPointerException as value has been set to null
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getSection("ints.values.ok"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `ints.values.ok` in configuration section");
    }

    @Test
    void unset() {
        // unset values
        config.unset("ints.values.ok");
        assertFalse(config.contains("ints.values.ok"));

        // unset section
        config.unset("bytes.values");
        assertFalse(config.contains("bytes.values"));

        // throws NullPointerException as value has been unset
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getSection("ints.values.ok"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `ints.values.ok` in configuration section");
    }

    @Test
    void createSection() {
        // does create section when there's none
        ConfigurationSection section = config.createSection("non.existing.key");
        assertSame(section, config.getSection("non.existing.key"));
        assertTrue(config.contains("non.existing.key"));

        // throws IllegalArgumentException if section already exists
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> config.createSection("ints.values.ok"));
        assertThat(e).hasMessageThat().isEqualTo("path `ints.values.ok` already exists in this configuration section");
    }

    @Test
    void getOrCreateSection() {
        // does create section when there's none
        ConfigurationSection section = config.getOrCreateSection("non.existing.key");
        assertSame(section, config.getSection("non.existing.key"));
        assertTrue(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping of different type from default already exists
        String path = "ints.values.ok";
        Object value = config.get(path);
        String ecMessage = new ConfigurationTypeException(path, ConfigurationSection.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getOrCreateSection(path));
        assertThat(ec).hasMessageThat().startsWith(ecMessage);
    }

    @Test
    void getSection() {
        // returns actual value if mapping is present
        assertTrue(config.contains("enums"));
        assertEquals(config.get("enums"), config.getSection("enums"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getSection("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is anything but a section
        String path = "ints.values.ok";
        Object value = config.get(path);
        String ecMessage = new ConfigurationTypeException(path, ConfigurationSection.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getSection(path));
        assertThat(ec).hasMessageThat().startsWith(ecMessage);
    }

    @Test
    void getSectionsDefault() {
        // returns actual value if mapping is present
        ConfigurationSection def = config.getSection("ints");
        assertTrue(config.contains("enums"));
        assertEquals(config.get("enums"), config.getSection("enums", def));

        // returns default value if mapping is missing
        assertNull(config.getSection("non.existing.key", null));
        assertSame(def, config.getSection("non.existing.key", def));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is anything but a section
        String path = "ints.values.ok";
        Object value = config.get(path);
        String ecMessage = new ConfigurationTypeException(path, ConfigurationSection.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getSection(path, def));
        assertThat(ec).hasMessageThat().startsWith(ecMessage);
    }

    @Test
    void getKeys() {
        ConfigurationSection section = config.getSection("keys");
        ConfigurationSection emptySection = config.createSection("non.existing.key");

        // root traversal
        assertEquals(Set.of("a", "g", "i"), section.getKeys(TraversalMode.ROOT));
        assertEquals(Collections.emptySet(), emptySection.getKeys(TraversalMode.ROOT));

        // branches traversal
        assertEquals(Set.of("a", "a.b", "a.b.c", "a.b.d", "a.e", "a.e.f", "g", "g.h", "i"), section.getKeys(TraversalMode.ALL));
        assertEquals(Collections.emptySet(), emptySection.getKeys(TraversalMode.ALL));

        // leaves traversal
        assertEquals(Set.of("a.b.c", "a.b.d", "a.e.f", "g.h", "i"), section.getKeys(TraversalMode.LEAVES));
        assertEquals(Collections.emptySet(), emptySection.getKeys(TraversalMode.LEAVES));
    }

    @Test
    void getByte() {
        // returns actual value if mapping is present
        assertEquals(Byte.MIN_VALUE, config.getByte("bytes.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getByte("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to byte
        String path = "bytes.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Byte.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getByte("bytes.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getByteDefault() {
        // returns actual value if mapping is present
        assertEquals(Byte.MIN_VALUE, config.getByte("bytes.values.ok", (byte) -1));

        // returns default value if mapping is missing
        assertEquals((byte) -1, config.getByte("non.existing.key", (byte) -1));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to byte
        String path = "bytes.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Byte.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getByte("bytes.values.cast", (byte) 2));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getByteList() {
        // returns actual value if mapping is present
        assertEquals(List.of((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE), config.getByteList("bytes.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getByteList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "bytes.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Byte.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getByteList("bytes.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to byte
        path = "bytes.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Byte.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getByteList("bytes.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getShort() {
        // returns actual value if mapping is present
        assertEquals((short) -32768, config.getShort("shorts.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getShort("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to short
        String path = "shorts.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Short.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getShort("shorts.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getShortDefault() {
        // returns actual value if mapping is present
        assertEquals((short) -32768, config.getShort("shorts.values.ok", (byte) -1));

        // returns default value if mapping is missing
        assertEquals((byte) -1, config.getShort("non.existing.key", (byte) -1));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to shorts
        String path = "shorts.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Short.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getShort("shorts.values.cast", (byte) -1));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getShortList() {
        // returns actual value if mapping is present
        assertEquals(List.of((short) 0, Short.MIN_VALUE, Short.MAX_VALUE), config.getShortList("shorts.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getShortList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "shorts.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Short.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getShortList("shorts.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to short
        path = "shorts.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Short.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getShortList("shorts.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getInt() {
        // returns actual value if mapping is present
        assertEquals(-2147483648, config.getInt("ints.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getInt("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to int
        String path = "ints.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Integer.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getInt("ints.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getIntDefault() {
        // returns actual value if mapping is present
        assertEquals(-2147483648, config.getInt("ints.values.ok", -1));

        // returns default value if mapping is missing
        assertEquals(-1, config.getInt("non.existing.key", -1));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to integer
        String path = "ints.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Integer.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getInt("ints.values.cast", -1));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getIntList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0, Integer.MIN_VALUE, Integer.MAX_VALUE), config.getIntList("ints.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getIntList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "ints.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Integer.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getIntList("ints.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to integer
        path = "ints.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Integer.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getIntList("ints.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getLong() {
        // returns actual value if mapping is present
        assertEquals(Long.MIN_VALUE, config.getLong("longs.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getLong("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to long
        String path = "longs.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Long.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getLong("longs.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getLongDefault() {
        // returns actual value if mapping is present
        assertEquals(-Long.MIN_VALUE, config.getLong("longs.values.ok", -1L));

        // returns default value if mapping is missing
        assertEquals(-1L, config.getLong("non.existing.key", -1L));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to long
        String path = "longs.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Long.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getLong("longs.values.cast", 10L));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getLongList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0L, Long.MIN_VALUE, Long.MAX_VALUE), config.getLongList("longs.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getLongList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "longs.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Long.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getLongList("longs.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to long
        path = "longs.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Long.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getLongList("longs.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getFloat() {
        // returns actual value if mapping is present
        assertEquals(-Float.MAX_VALUE, config.getFloat("floats.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getFloat("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to float
        String path = "floats.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Float.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getFloat("floats.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getFloatDefault() {
        // returns actual value if mapping is present
        assertEquals(-Float.MAX_VALUE, config.getFloat("floats.values.ok", -1.0f));

        // returns default value if mapping is missing
        assertEquals(-1.0f, config.getFloat("non.existing.key", -1.0f));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to float
        String path = "floats.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Float.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getFloat("floats.values.cast", -1.0f));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getFloatList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0f, -Float.MAX_VALUE, Float.MAX_VALUE), config.getFloatList("floats.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getFloatList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "floats.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Float.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getFloatList("floats.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to float
        path = "floats.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Float.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getFloatList("floats.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getDouble() {
        // returns actual value if mapping is present
        assertEquals(-Double.MAX_VALUE, config.getDouble("doubles.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getDouble("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to double
        String path = "doubles.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Double.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getDouble("doubles.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getDoubleDefault() {
        // returns actual value if mapping is present
        assertEquals(-Double.MAX_VALUE, config.getDouble("doubles.values.ok", -1.0D));

        // returns default value if mapping is missing
        assertEquals(-1.0D, config.getDouble("non.existing.key", -1.0D));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to double
        String path = "doubles.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Double.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getDouble("doubles.values.cast", -1.0f));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getDoubleList() {
        // returns actual value if mapping is present
        assertEquals(List.of(0.0D, -Double.MAX_VALUE, Double.MAX_VALUE), config.getDoubleList("doubles.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getDoubleList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "doubles.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Double.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getDoubleList("doubles.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to double
        path = "doubles.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Double.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getDoubleList("doubles.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getBoolean() {
        // returns actual value if mapping is present
        assertTrue(config.getBoolean("booleans.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getBoolean("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to boolean
        String path = "booleans.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Boolean.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getBoolean("booleans.values.cast"));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getBooleanDefault() {
        // returns actual value if mapping is present
        assertTrue(config.getBoolean("booleans.values.ok", false));

        // returns default value if mapping is missing
        assertTrue(config.getBoolean("non.existing.key", true));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present contains any value that cannot be converted to boolean
        String path = "booleans.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, Boolean.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getBoolean("booleans.values.cast", true));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getBooleanList() {
        // returns actual value if mapping is present
        assertEquals(List.of(true, false), config.getBooleanList("booleans.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getDoubleList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "booleans.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, Boolean.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getBooleanList("booleans.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to boolean
        path = "booleans.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, Boolean.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getBooleanList("booleans.lists.cast"));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getString() {
        // returns actual value if mapping is present
        assertEquals("3", config.getString("strings.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getString("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // never throws ConfigurationTypeException as any object has its string representation
    }

    @Test
    void getStringDefault() {
        // returns actual value if mapping is present
        assertEquals("3", config.getString("strings.values.ok"));

        // returns default value if mapping is missing
        assertEquals("tmp string", config.getString("non.existing.key", "tmp string"));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // never throws ConfigurationTypeException as any object has its string representation
    }

    @Test
    void getStringList() {
        // returns actual value if mapping is present
        assertEquals(List.of("hello", "Stami", ""), config.getStringList("strings.lists.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getStringList("non.existing.key"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "strings.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, String.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getStringList("strings.lists.null"));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // never throws ConfigurationTypeException as any object has its string representation
    }

    @Test
    void getEnum() {
        // returns actual value if mapping is present
        assertEquals(TestEnum.TEST_1, config.getEnum("enums.values.ok", TestEnum.class));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getEnum("non.existing.key", TestEnum.class));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationTypeException if mapping is present but cannot be converted to enum
        String path = "enums.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, TestEnum.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getEnum("enums.values.cast", TestEnum.class));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getEnumDefault() {
        // returns actual value if mapping is present
        assertEquals(TestEnum.TEST_1, config.getEnum("enums.values.ok", TestEnum.class));

        // returns default value if mapping is missing
        assertEquals(TestEnum.TEST_2, config.getEnum("non.existing.key", TestEnum.class, TestEnum.TEST_2));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));

        // throws ConfigurationTypeException if mapping is present but cannot be converted to enum
        String path = "enums.values.cast";
        Object value = config.get(path);
        String message = new ConfigurationTypeException(path, TestEnum.class, value).getMessage();
        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getEnum("enums.values.cast", TestEnum.class, TestEnum.TEST_1));
        assertThat(ec).hasMessageThat().isEqualTo(message);
    }

    @Test
    void getEnumList() {
        // returns actual value if mapping is present
        assertEquals(List.of(TestEnum.TEST_1, TestEnum.TEST_2), config.getEnumList("enums.lists.ok", TestEnum.class));

        // throws NullPointerException if mapping is missing
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getEnumList("non.existing.key", TestEnum.class));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");

        // throws ConfigurationListTypeException if any entry is null
        String path = "enums.lists.null";
        List<Object> list = config.getList(path);
        Object value = list.get(list.size() - 1);
        String message = new ConfigurationListTypeException(path, TestEnum.class, list, value).getMessage();
        ConfigurationListTypeException e = assertThrows(ConfigurationListTypeException.class, () -> config.getEnumList("enums.lists.null", TestEnum.class));
        assertThat(e).hasMessageThat().isEqualTo(message);

        // throws ConfigurationListTypeException if any entry could not be converted to enum
        path = "enums.lists.cast";
        list = config.getList(path);
        value = list.get(list.size() - 1);
        message = new ConfigurationListTypeException(path, TestEnum.class, list, value).getMessage();
        e = assertThrows(ConfigurationListTypeException.class, () -> config.getEnumList("enums.lists.cast", TestEnum.class));
        assertThat(e).hasMessageThat().isEqualTo(message);
    }

}
