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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTests {

    @TempDir
    Path directory;

    Configuration config;

    @BeforeEach
    void beforeEach() {
        String file = "test_config.yml";
        File configFile = directory.resolve(file).toFile();

        config = ConfigurationBuilder
                .fromFile(configFile)
                .setDefaultResource(file)
                .build();
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
        assertEquals(-2147483648, config.get("ints.values.ok"));

        // throws NullPointerException if mapping is missing
        NullPointerException e = assertThrows(NullPointerException.class, () -> config.get("non.existing.key"));
        assertThat(e).hasMessageThat().isEqualTo("no mapping found for path `non.existing.key` in configuration section");
    }

    @Test
    void getDefault() {
        // any object
        Object def = new Object();

        // returns actual value if mapping is present
        assertEquals(-2147483648, config.get("ints.values.ok", def));

        // returns default value if mapping is missing
        assertNull(config.get("non.existing.key", null));
        assertSame(def, config.get("non.existing.key", def));

        // does not store default value
        assertFalse(config.contains("non.existing.key"));
    }

//    @Test
//    @SuppressWarnings("all") // otherwise complains about assertEquals on incompatible types
//    void getOrSet() {
//        // any object
//        String def = "weeee :D";
//
//        // returns actual value if mapping is present
//        assertEquals(-2147483648, config.getOrSet("ints.values.ok", 3));
//
//        // returns default value if mapping is missing
//        assertSame(def, config.getOrSet("non.existing.key", def));
//
//        // throws ConfigurationTypeException if mapping of different type from default already exists
//        String path = "ints.values.ok";
//        Object value = config.get(path);
//        String ecMessage = new ConfigurationTypeException(path, String.class, value).getMessage();
//        ConfigurationTypeException ec = assertThrows(ConfigurationTypeException.class, () -> config.getOrSet(path, def));
//        assertThat(ec).hasMessageThat().startsWith(ecMessage);
//
//        // does store default value
//        assertSame(def, config.get("non.existing.key"));
//    }

    @Test
    void getList() {
        // returns actual value if mapping is present
        List<Object> list = new ArrayList<>();
        list.add(0);
        list.add("A");
        list.add(null);
        list.add("");
        list.add(true);
        list.add(List.of(2, 1));
        list.add(null);
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

        // set null
        config.set("ints.values.ok", null);

        // section no longer contains value
        assertFalse(config.contains("ints.values.ok"));

        // throws NullPointerException as value has been set to null
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getSection("ints.values.ok"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `ints.values.ok` in configuration section");
    }

    @Test
    void unset() {
        // unset
        config.unset("ints.values.ok");

        // section no longer contains value
        assertFalse(config.contains("ints.values.ok"));

        // throws NullPointerException as value has been unset
        NullPointerException en = assertThrows(NullPointerException.class, () -> config.getSection("ints.values.ok"));
        assertThat(en).hasMessageThat().isEqualTo("no mapping found for path `ints.values.ok` in configuration section");
    }

    @Test
    void createSection() {
        // does create section when there's none
        ConfigurationSection section = config.createSection("non.existing.key");
        assertSame(section, config.getSection("non.existing.key"));

        // throws IllegalArgumentException if section already exists
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> config.createSection("ints.values.ok"));
        assertThat(e).hasMessageThat().isEqualTo("path `ints.values.ok` already exists in this configuration section");
    }

    @Test
    void getOrCreateSection() {
        // does create section when there's none
        ConfigurationSection section = config.getOrCreateSection("non.existing.key");
        assertSame(section, config.getSection("non.existing.key"));

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
        String ecMessage = new ConfigurationTypeException(path,ConfigurationSection.class, value).getMessage();
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
        assertEquals(Set.of("a", "a.b", "a.b.c", "a.b.d", "a.e", "a.e.f", "g", "g.h", "i"), section.getKeys(TraversalMode.BRANCHES));
        assertEquals(Collections.emptySet(), emptySection.getKeys(TraversalMode.BRANCHES));

        // leaves traversal
        assertEquals(Set.of("a.b.c", "a.b.d", "a.e.f", "g.h", "i"), section.getKeys(TraversalMode.LEAVES));
        assertEquals(Collections.emptySet(), emptySection.getKeys(TraversalMode.LEAVES));
    }

    @Test
    void getByte() {
        // returns actual value if mapping is present
        assertEquals((byte) 1, config.getByte("bytes.values.ok"));

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
        assertEquals((byte) 1, config.getByte("bytes.values.ok", (byte) -1));

        // returns default value if mapping is missing
        assertEquals((byte) 2, config.getByte("non.existing.key", (byte) 2));

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
        assertEquals(-1L, config.getLong("longs.values.ok"));

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
        assertEquals(-1L, config.getLong("longs.values.ok", 10L));

        // returns default value if mapping is missing
        assertEquals(10L, config.getLong("non.existing.key", 10L));

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
        assertEquals(3.4f, config.getFloat("floats.values.ok"));

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
        assertEquals(3.4f, config.getFloat("floats.values.ok", -1.0f));

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
        assertEquals(Double.MAX_VALUE, config.getDouble("doubles.values.ok"));

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
        assertEquals(Double.MAX_VALUE, config.getDouble("doubles.values.ok", -1.0D));

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
        assertEquals(List.of(0D, -Double.MAX_VALUE, Double.MAX_VALUE), config.getDoubleList("doubles.lists.ok"));

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
