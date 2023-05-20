package net.bobolabs.config;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class ConfigurationManagerTest {

    @TempDir
    static Path directory;

    @BeforeAll
    static void beforeAll() {
        ConfigurationManager<TestConfigs> manager = new ConfigurationManager<>(directory.toFile(), TestConfigs.class);

    }

}
