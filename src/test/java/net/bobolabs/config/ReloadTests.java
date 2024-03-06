/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2024 BoboLabs.net
 * Copyright (C) 2024 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2024 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2024 Third party contributors
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReloadTests {

    static final String FILE_NAME = "empty_config.yml";

    @TempDir
    Path directory;

    Configuration config;

    @BeforeEach
    void beforeEach() {
        config = ConfigurationLoader
                .fromFile(directory.toFile(), FILE_NAME)
                .setDefaultResource(FILE_NAME)
                .load();

        // set base values
        config.set("bytes.value", (byte) 1);
        config.set("shorts.value", (short) 1);
        config.set("ints.value", 1);
        config.set("longs.value", 1L);
        config.set("floats.value", 1.0f);
        config.set("doubles.value", 1.0D);
        config.set("booleans.value", true);
        config.set("strings.value", "hello");
        config.set("enums.value", TestEnum.TEST_1);

        // set list values
        config.set("bytes.list", List.of((byte) 1, (byte) 2));
        config.set("shorts.list", List.of((short) 1, (short) 2));
        config.set("ints.list", List.of(1, 2));
        config.set("longs.list", List.of(1L, 2L));
        config.set("floats.list", List.of(1f, 2f));
        config.set("doubles.list", List.of(1D, 2D));
        config.set("booleans.list", List.of(true, false));
        config.set("strings.list", List.of("hello", "Bobo"));
        config.set("enums.list", List.of(TestEnum.TEST_1, TestEnum.TEST_2));
    }

    @Test
    void worksInMemory() {
        // check base values match the expected ones
        assertEquals((byte) 1, config.getByte("bytes.value"));
        assertEquals((short) 1, config.getShort("shorts.value"));
        assertEquals(1, config.getInt("ints.value"));
        assertEquals(1L, config.getLong("longs.value"));
        assertEquals(1.0f, config.getFloat("floats.value"));
        assertEquals(1.0D, config.getDouble("doubles.value"));
        assertTrue(config.getBoolean("booleans.value"));
        assertEquals("hello", config.getString("strings.value"));
        assertEquals(TestEnum.TEST_1, config.getEnum("enums.value", TestEnum.class));

        // check list values match the expected ones
        assertEquals(List.of((byte) 1, (byte) 2), config.getByteList("bytes.list"));
        assertEquals(List.of((short) 1, (short) 2), config.getShortList("shorts.list"));
        assertEquals(List.of(1, 2), config.getIntList("ints.list"));
        assertEquals(List.of(1L, 2L), config.getLongList("longs.list"));
        assertEquals(List.of(1f, 2f), config.getFloatList("floats.list"));
        assertEquals(List.of(1D, 2D), config.getDoubleList("doubles.list"));
        assertEquals(List.of(true, false), config.getBooleanList("booleans.list"));
        assertEquals(List.of("hello", "Bobo"), config.getStringList("strings.list"));
        assertEquals(List.of(TestEnum.TEST_1, TestEnum.TEST_2), config.getEnumList("enums.list", TestEnum.class));

        // reload
        config.reload();

        // there should be no keys left because changes were not written on
        // disk therefore the file that was reloaded was supposed to be empty
        assertEquals(Collections.emptySet(), config.getKeys(TraversalMode.ALL));
    }

    @Test
    void worksFromFile() throws IOException {
        // check base values match the expected ones
        assertEquals((byte) 1, config.getByte("bytes.value"));
        assertEquals((short) 1, config.getShort("shorts.value"));
        assertEquals(1, config.getInt("ints.value"));
        assertEquals(1L, config.getLong("longs.value"));
        assertEquals(1.0f, config.getFloat("floats.value"));
        assertEquals(1.0D, config.getDouble("doubles.value"));
        assertTrue(config.getBoolean("booleans.value"));
        assertEquals("hello", config.getString("strings.value"));
        assertEquals(TestEnum.TEST_1, config.getEnum("enums.value", TestEnum.class));

        // check list values match the expected ones
        assertEquals(List.of((byte) 1, (byte) 2), config.getByteList("bytes.list"));
        assertEquals(List.of((short) 1, (short) 2), config.getShortList("shorts.list"));
        assertEquals(List.of(1, 2), config.getIntList("ints.list"));
        assertEquals(List.of(1L, 2L), config.getLongList("longs.list"));
        assertEquals(List.of(1f, 2f), config.getFloatList("floats.list"));
        assertEquals(List.of(1D, 2D), config.getDoubleList("doubles.list"));
        assertEquals(List.of(true, false), config.getBooleanList("booleans.list"));
        assertEquals(List.of("hello", "Bobo"), config.getStringList("strings.list"));
        assertEquals(List.of(TestEnum.TEST_1, TestEnum.TEST_2), config.getEnumList("enums.list", TestEnum.class));

        // swap file on disk
        try (InputStream in = this.getClass().getResourceAsStream("/swap_reload_config.yml")) {
            Files.copy(Objects.requireNonNull(in), directory.resolve(FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
        }

        // reload
        config.reload();

        // check base values differ from before
        config.set("bytes.value", (byte) 2);
        config.set("shorts.value", (short) 2);
        config.set("ints.value", 2);
        config.set("longs.value", 2L);
        config.set("floats.value", 2.0f);
        config.set("doubles.value", 2.0D);
        config.set("booleans.value", true);
        config.set("strings.value", "hello");
        config.set("enums.value", TestEnum.TEST_1);

        // check base lists differ from before
        config.set("bytes.list", List.of((byte) 3, (byte) 4));
        config.set("shorts.list", List.of((short) 3, (short) 4));
        config.set("ints.list", List.of(3, 4));
        config.set("longs.list", List.of(3L, 4L));
        config.set("floats.list", List.of(3f, 4f));
        config.set("doubles.list", List.of(3D, 4D));
        config.set("booleans.list", List.of(false, true));
        config.set("strings.list", List.of("good bye", "Bobo"));
        config.set("enums.list", List.of(TestEnum.TEST_2, TestEnum.TEST_1));
    }

}
