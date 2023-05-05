import config.Configuration;
import config.ConfigurationManager;
import org.junit.jupiter.api.Test;

public class Main {


    @Test
    public void test() {
        ConfigurationManager<TestEnum> configManager = new ConfigurationManager<>(null, TestEnum.class);
        configManager.enable();
        Configuration config = configManager.configuration(TestEnum.CONFIG_4);

    }


}
