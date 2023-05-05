import config.Config;
import config.ConfigurationDescription;

public enum TestEnum implements ConfigurationDescription {
/*
* 1: Fa
* 2: Non salva la default resource
* 3: Prende il config da configs/config3.yml
* 4: Non ha senso
* */

    @Config(autoSave = true)
    CONFIG_1, // 1

    @Config(autoSave = true, saveDefaultResource = false)
    CONFIG_2, // 2

    @Config(defaultResource = "configs/config3.yml")
    CONFIG_3, // 3

    @Config(saveDefaultResource = false)
    CONFIG_4, // 4

    @Config(path = "custom/config_5.yml")
    CONFIG_5, // 5

    @Config(path = "custom/config_6.yml", autoSave = true)
    CONFIG_6, // 6

    CONFIG_7, // 7

}
