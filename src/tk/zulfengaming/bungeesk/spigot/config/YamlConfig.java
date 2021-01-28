package tk.zulfengaming.bungeesk.spigot.config;

import org.bukkit.configuration.file.FileConfiguration;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;

public class YamlConfig {

    public BungeeSkSpigot instance;

    public FileConfiguration configObject;

    public YamlConfig(BungeeSkSpigot instanceIn) {
        this.instance = instanceIn;

        instance.saveDefaultConfig();

        this.configObject = instanceIn.getConfig();

    }

    public void addDefault(String node, Object value) {
        configObject.addDefault(node, value);

    }

    public String getString(String node) {
        return configObject.getString(node);
    }

    public boolean getBoolean(String node) {
        return configObject.getBoolean(node);
    }

    public int getInt(String node) {
        return configObject.getInt(node);
    }
}
