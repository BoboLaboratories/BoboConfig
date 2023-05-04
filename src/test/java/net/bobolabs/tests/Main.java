package net.bobolabs.tests;

import net.bobolabs.config.ConfigurationManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    private static final File testDataFolder = new File("./.tmp");
    @BeforeAll
    public static void setUp() {
        if (!testDataFolder.exists()) {
            testDataFolder.mkdirs();
        }
    }

//    @AfterAll
//    public static void finished() throws IOException {
//        if (testDataFolder.exists()) {
//            Files.delete(testDataFolder.toPath());
//        }
//    }


    @Test
    public void test() {
        ConfigurationManager<TestConfigs> configManager = new ConfigurationManager<>(testDataFolder, TestConfigs.class);
        configManager.onEnable();
        String str = configManager.get(TestConfigs.CONFIG).getString("key", "D:");
        String str1 = configManager.get(TestConfigs.CONFIG_1).getString("key1", "D:");
        String str2 = configManager.get(TestConfigs.CONFIG2).getString("key2", "D:");
        System.out.println(str);
        System.out.println(str1);
        System.out.println(str2);

        configManager.onDisable();

    }

}
