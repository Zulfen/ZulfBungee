package tk.zulfengaming.bungeesk.bungeecord.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;

import java.io.*;

public class YamlConfig {

    public BungeeSkProxy instance;

    public File configFile;

    private ConfigurationProvider configObject;

    private Configuration loadedConfig;

    public YamlConfig(BungeeSkProxy instanceIn) {
        this.instance = instanceIn;

        this.configFile = new File(instance.getDataFolder(), "config.yml");

        try {
            if (!instance.getDataFolder().exists()) {
                instance.getDataFolder().mkdir();
            }

            // Thank you https://www.spigotmc.org/members/tux.2180/ <3

            if (!configFile.exists())

                try {

                    boolean created = configFile.createNewFile();

                    if (created) {
                        try (InputStream is = instance.getResourceAsStream("bungeecord.yml");
                             OutputStream os = new FileOutputStream(configFile)) {
                            ByteStreams.copy(is, os);
                        }
                    }

                } catch (IOException e) {
                    instance.error("There was an error copying the default config:");
                    e.printStackTrace();
                }

            this.configObject = ConfigurationProvider.getProvider(YamlConfiguration.class);

            this.loadedConfig = configObject.load(configFile);

        } catch (IOException e) {
            instance.error("There was an error getting the config!");

            e.printStackTrace();
        }

    }

    public void save(String node, Object value) throws IOException {
        loadedConfig.set(node, value);
        configObject.save(loadedConfig, configFile);

    }

    public String getString(String node) {
        return loadedConfig.getString(node);
    }

    public boolean getBoolean(String node) {
        return loadedConfig.getBoolean(node);
    }

    public int getInt(String node) {
        return loadedConfig.getInt(node);
    }
}
