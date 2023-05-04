package net.bobolabs.tests;


import net.bobolabs.config.Config;
import net.bobolabs.config.Configurable;

public enum TestConfigs implements Configurable {

    @Config
    CONFIG,

    @Config(path = "configs/config1.yml", autoSave = true, defaultResource = "config_1.yml")
    CONFIG_1,

    @Config(path = "config_2.yml", autoSave = true, defaultResource = "configs/config2.yml")
    CONFIG2,


}