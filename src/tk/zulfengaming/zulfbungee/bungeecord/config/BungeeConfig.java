package tk.zulfengaming.zulfbungee.bungeecord.config;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;

import java.io.IOException;
import java.util.List;

public class BungeeConfig extends ProxyConfig<ProxyServer, ProxiedPlayer> {

    private final Configuration loadedConfig;

    public BungeeConfig(ZulfBungeecord instanceIn) {

        super(instanceIn);

        ConfigurationProvider configObject = ConfigurationProvider.getProvider(YamlConfiguration.class);

        try {
            this.loadedConfig = configObject.load(configFile.toFile());
        } catch (IOException e) {
            instanceIn.error("Error loading config on bungee:");
            throw new RuntimeException(e);
        }

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
