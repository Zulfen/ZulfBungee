package com.zulfen.zulfbungee.bungeecord.config;

import com.zulfen.zulfbungee.bungeecord.interfaces.ZulfBungeecordImpl;
import com.zulfen.zulfbungee.core.config.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BungeeConfig extends ProxyConfig<ProxyServer, ProxiedPlayer, Configuration> {

    public BungeeConfig(ZulfBungeecordImpl instanceIn) {
        super(instanceIn);
    }

    @Override
    public Configuration loadConfig(Path configPath) {
        ConfigurationProvider configObject = ConfigurationProvider.getProvider(YamlConfiguration.class);
        try {
            return configObject.load(configPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String node) {
        return loadedConfigObject.getString(node);
    }

    public boolean getBoolean(String node) {
        return loadedConfigObject.getBoolean(node);
    }

    public int getInt(String node) {
        return loadedConfigObject.getInt(node);
    }

    public List<Integer> getIntList(String node) {
        return loadedConfigObject.getIntList(node);
    }

}
