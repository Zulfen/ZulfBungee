package tk.zulfengaming.zulfbungee.bungeecord.config;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BungeeConfig extends ProxyConfig<ProxyServer> {

    private final Configuration loadedConfig;

    public BungeeConfig(ZulfBungeecord instanceIn) {

        super(instanceIn);

        ConfigurationProvider configObject = ConfigurationProvider.getProvider(YamlConfiguration.class);

        try {
            this.loadedConfig = configObject.load(configFile);
        } catch (IOException e) {
            instanceIn.error("Error loading config on bungee:");
            throw new RuntimeException(e);
        }

    }

    public List<String> getScripts() {

        ArrayList<String> cachedScripts = new ArrayList<>();

        if (scriptsFolder.exists()) {

            File[] files = scriptsFolder.listFiles(File::isFile);

            if (files != null) {

                for (File file : files) {

                    String name = file.getName();

                    if (name.endsWith(".sk")) {
                        cachedScripts.add(name);
                    }

                }
            }
        }

        return cachedScripts;
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

    public List<Integer> getIntList(String node) {
        return loadedConfig.getIntList(node);
    }

}
