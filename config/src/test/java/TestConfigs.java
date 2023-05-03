import net.bobolabs.config.Configurable;
import net.bobolabs.config.annotation.Config;

public enum TestConfigs implements Configurable {

    @Config
    CONFIG,

    @Config(path = "configs/config_1.yml", autoSave = true)
    CONFIG_1,

    @Config(path = "configs/config2.yml")
    CONFIG2,


}
