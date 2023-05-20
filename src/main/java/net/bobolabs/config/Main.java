package net.bobolabs.config;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Configuration config = ConfigurationBuilder
                .fromFile("config.yml")
                .build();

        config.set("map1", new HashMap<>());
        config.save();
        ConfigurationSection s1 = config.getSection("map1");
        config.reload();
        ConfigurationSection s2 = config.createSection("map2");
        config.save();
        System.out.println(s1.equals(s2));
    }

}
