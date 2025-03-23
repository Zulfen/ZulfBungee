package com.zulfen.zulfbungee.velocity.config;

import io.leangen.geantyref.TypeToken;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import com.zulfen.zulfbungee.core.config.ProxyConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class VelocityConfig extends ProxyConfig<ProxyServer, Player, ConfigurationNode> {

    public VelocityConfig(ZulfVelocityImpl instanceIn) {
        super(instanceIn);
    }

    @Override
    public ConfigurationNode loadConfig(Path configPath) {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .build();
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getString(String node) {
        return loadedConfigObject.node(node).getString();
    }

    @Override
    public boolean getBoolean(String node) {
        return loadedConfigObject.node(node).getBoolean();
    }

    @Override
    public int getInt(String node) {
        return loadedConfigObject.node(node).getInt();
    }

    @Override
    public List<Integer> getIntList(String node) {
        try {
            return loadedConfigObject.node(node).getList(TypeToken.get(Integer.class));
        } catch (SerializationException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
