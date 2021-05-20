package tk.zulfengaming.zulfbungee.spigot.config;

import org.bukkit.configuration.file.FileConfiguration;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

public class YamlConfig {

    public final ZulfBungeeSpigot instance;

    public final FileConfiguration configObject;

    public YamlConfig(ZulfBungeeSpigot instanceIn) {
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
