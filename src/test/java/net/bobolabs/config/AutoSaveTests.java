/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Third party contributors
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.bobolabs.config.TestUtils.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoSaveTests {

    private static final String EXPECTED_FILE_CONTENTS = """
            bytes:
              values:
                ok: -128
                cast: true
              lists:
                ok:
                - 0
                - -128
                - 127
                'null':
                - 0
                - -128
                - 127
                - null
                cast:
                - 0
                - -128
                - 127
                - []
            shorts:
              values:
                ok: -32768
                cast: ''
              lists:
                ok:
                - 0
                - -32768
                - 32767
                'null':
                - 0
                - -32768
                - 32767
                - null
                cast:
                - 0
                - -32768
                - 32767
                - []
            ints:
              values:
                ok: -2147483648
                cast: []
              lists:
                ok:
                - 0
                - -2147483648
                - 2147483647
                'null':
                - 0
                - -2147483648
                - 2147483647
                - null
                cast:
                - 0
                - -2147483648
                - 2147483647
                - []
            longs:
              values:
                ok: -9223372036854775808
                cast: {}
              lists:
                ok:
                - 0
                - -9223372036854775808
                - 9223372036854775807
                'null':
                - 0
                - -9223372036854775808
                - 9223372036854775807
                - null
                cast:
                - 0
                - -9223372036854775808
                - 9223372036854775807
                - ''
            floats:
              values:
                ok: -3.4028235E38
                cast: weeeee :D
              lists:
                ok:
                - 0.0
                - -3.4028235E38
                - 3.4028235E38
                'null':
                - 0.0
                - -3.4028235E38
                - 3.4028235E38
                - null
                cast:
                - 0.0
                - -3.4028235E38
                - 3.4028235E38
                - hello
            doubles:
              values:
                ok: -1.7976931348623157E308
                cast: []
              lists:
                ok:
                - 0.0
                - -1.7976931348623157E308
                - 1.7976931348623157E308
                'null':
                - 0.0
                - -1.7976931348623157E308
                - 1.7976931348623157E308
                - null
                cast:
                - 0.0
                - -1.7976931348623157E308
                - 1.7976931348623157E308
                - - another_list
            booleans:
              values:
                ok: true
                cast: 34
              lists:
                ok:
                - true
                - false
                'null':
                - true
                - false
                - null
                cast:
                - true
                - false
                - -0.6
            strings:
              values:
                ok: 3
              lists:
                ok:
                - hello
                - Stami
                - ''
                'null':
                - hello
                - Stami
                - ''
                - null
            enums:
              values:
                ok: TEST_1
                cast: TEST_3
              lists:
                ok:
                - TEST_1
                - TEST_2
                'null':
                - TEST_1
                - TEST_2
                - null
                cast:
                - TEST_1
                - TEST_2
                - 3
            objects:
              list:
              - 0
              - A
              - null
              - ''
              - true
              - - 2
                - 1
              - null
              - []
              - {}
            keys:
              i: 1
              a:
                b:
                  c: 2
                  d: 3
                e:
                  f: 4
              g:
                h: 5
            """;

    static final String FILE_NAME = "empty_config.yml";

    @TempDir
    Path directory;

    Configuration config;

    @Test
    void autoSaveWorks() throws IOException, URISyntaxException {
        File configFile = new File(directory.toFile(), FILE_NAME);

        config = ConfigurationLoader
                .fromFile(configFile)
                .setDefaultResource(FILE_NAME)
                .autoSave(true)
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

        // we do not call .save() manually

//        URL resourceUrl = getClass().getClassLoader().getResource("test_config.yml");
//        String expected = Files.readString(Paths.get(Objects.requireNonNull(resourceUrl).toURI()));
        String saved = Files.readString(configFile.toPath());

        assertEquals(saved, EXPECTED_FILE_CONTENTS);
    }

}
