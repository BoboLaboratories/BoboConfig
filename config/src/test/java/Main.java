import net.bobolabs.config.ConfigManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    private static final File testDataFolder = new File(".tmp");

    @BeforeAll
    public static void setUp() {
        if (!testDataFolder.exists()) {
            testDataFolder.mkdirs();
        }
    }

    @AfterAll
    public static void finished() throws IOException {
        if (testDataFolder.exists()) {
            Files.delete(testDataFolder.toPath());
        }
    }


    @Test
    public void test() {
        System.out.println(System.getProperty("user.dir"));
        ConfigManager<TestConfigs> configManager = new ConfigManager<>(testDataFolder, TestConfigs.class);
        configManager.onEnable();

        String str = configManager.getConfig(TestConfigs.CONFIG).getString("roba-seria", "");
        System.out.println(str);

        configManager.onDisable();


    }


}
