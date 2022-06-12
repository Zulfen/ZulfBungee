package tk.zulfengaming.zulfbungee.spigot.config;

import org.bukkit.configuration.file.FileConfiguration;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

public class YamlConfig {

    private final FileConfiguration configObject;

    public YamlConfig(ZulfBungeeSpigot instanceIn) {

        instanceIn.saveDefaultConfig();

        this.configObject = instanceIn.getConfig();

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
