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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class ConfigurationManagerTest {

    @TempDir
    static Path directory;

    static ConfigurationManager<TestConfigs> manager;

    @BeforeAll
    static void beforeAll() {
        manager = new ConfigurationManager<>(directory.toFile(), TestConfigs.class);
    }

    @Test
    void testDefault() {
        Config config = manager.makeConfig(TestConfigs.DEFAULT);
        assertEquals("default.yml", config.path());
        assertEquals("default.yml", config.defaultResource());
        assertTrue(config.saveDefaultResource());
        assertFalse(config.autoSave());
    }

    @Test
    void testDefaultResource() {
        Config config = manager.makeConfig(TestConfigs.DEFAULT_RESOURCE);
        assertEquals("default_resource.yml", config.path());
        assertEquals("some_resource.yml", config.defaultResource());
        assertTrue(config.saveDefaultResource());
        assertFalse(config.autoSave());
    }

    @Test
    void testSaveDefaultResource() {
        Config config = manager.makeConfig(TestConfigs.SAVE_DEFAULT_RESOURCE);
        assertEquals("save_default_resource.yml", config.path());
        assertEquals("save_default_resource.yml", config.defaultResource());
        assertFalse(config.saveDefaultResource());
        assertFalse(config.autoSave());
    }

    @Test
    void testAutoSave() {
        Config config = manager.makeConfig(TestConfigs.AUTO_SAVE);
        assertEquals("auto_save.yml", config.path());
        assertEquals("auto_save.yml", config.defaultResource());
        assertTrue(config.saveDefaultResource());
        assertTrue(config.autoSave());
    }


    enum TestConfigs implements ConfigurationDescription {

        DEFAULT,

        @Config(path = "some_path.yml")
        PATH,

        @Config(defaultResource = "some_resource.yml")
        DEFAULT_RESOURCE,

        @Config(saveDefaultResource = false)
        SAVE_DEFAULT_RESOURCE,

        @Config(autoSave = true)
        AUTO_SAVE

    }

}
