package net.bobolabs.utils;

public interface Reloadable {
    void onEnable();
    void onDisable();

    default void reload(){
        onDisable();
        onEnable();
    }

}
