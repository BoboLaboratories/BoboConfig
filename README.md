### BoboLabs

We write code, you enjoy it :D

<big><pre>
&nbsp;wonders =&nbsp;[**BoboLabs**](https://www.bobolabs.net)
&nbsp;&nbsp;&nbsp;.add([**glowy**](https://www.fabionebbia.com))
&nbsp;&nbsp;&nbsp;.add([**stami**](https://stami.bobolabs.net))
&nbsp;&nbsp;&nbsp;.code();
&nbsp;
&nbsp;you.enjoy(wonders);
</pre></big>

<br>

## BoboConfig

A thread-safe YAML 1.1 configuration library which provides reload and auto-save capabilities. Also very ✨professional✨.

#### Main characteristics
- finely tuned for *Minecraft* development (Bukkit, Spigot, Paper, Folia, Bungee, Velocity, etc.), but also compatible with any sort of Java project
- flexible and _customizable_ usage in both simple and complex projects (see the examples below)
- _thread-safe_ (especially useful for Folia projects due to its multithreaded nature)
- easily handles _multiple_ configuration files
- both manual and _auto save_ support
- fully _reloadable_ configurations

<br>

|    Vanilla Java    |    Spigot/Paper    |     BungeeCord     |      Velocity      | Folia |
|:------------------:|:------------------:|:------------------:|:------------------:|:-----:|
| :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |  💡   |

<br>

### Usage

Here are the full [Javadocs](https://docs.bobolabs.net/config/).

#### Gradle
```gradle
repositories {
    maven { url = 'https://repo.bobolabs.net/repository/maven-public/' }
}

dependencies {
    implementation 'net.bobolabs.config:config:2.0.0'
}
```

#### Maven
```xml
<repositories>
    <repository>
        <id>BoboLabs</id>
        <url>https://repo.bobolabs.net/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.bobolabs.config</groupId>
        <artifactId>config</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

<br>

### Simple usage
  ```java
  Configuration config = ConfigurationLoader
      .fromFile(getDataFolder(), "config.yml")
      .setDefaultResource("dir/config.yml")    // Optional
      .autoSave(true)                          // Optional
      .load();
  
  String data = config.getString("my.data");
  ```

<br>

### Full fledged usage

Describe the configuration files you need by declaring enum fields optionally marked with `@Config`.  
`@Config` accepts several options to meet your needs, find out more [here](TODO).

  ```java
  public enum Configs implements ConfigurationDescription {
  
      CONFIG,
  
      @Config(path = "configs/gui.yml")
      GUI,
  
      @Config(path = "configs/users.yml", autoSave = true)
      USER_DATA,
      
      @Config(path = "world.yml", defaultResource = "configs/world.yml", autoSave = true)
      WORLD_SETTINGS,

      @Config(path = "optional.yml", saveDefaultResource = false)
      OPTIONAL_CONFIG
  
  }
  ```

<br>

Then proceed by creating a `ConfigurationManager` which will load the files according to the provided enum.

```java
ConfigurationManager<Configs> configurationManager = new ConfigurationManager<>(getDataFolder(), Configs.class);
```

<br>

The newly created manager can now be used to retrieve single configuration objects which can be used as normal.

```java
Configuration config = configurationManager.load(Configs.GUI);
String guiTitle = config.getString("gui.title");
int guiSize = config.getInt("gui.size");
```

<br>

### Example usage within a Spigot plugin

```java
public final class MyPlugin extends JavaPlugin {

    private ConfigurationManager<Configs> configurationManager;

    @Override
    public void onEnable() {
        configurationManager = new ConfigurationManager<>(getDataFolder(), Configs.class);
        configurationManager.loadAll();
    }

    @Override
    public void onDisable() {
        if (configurationManager != null) {
            configurationManager.unloadAll();
            configurationManager = null;
        }
    }

    public @NotNull Configuration getConfiguration(@NotNull Configs config) {
        return configurationManager.get(config);
    }

}
```
