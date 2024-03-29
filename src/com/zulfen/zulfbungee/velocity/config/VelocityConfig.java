package com.zulfen.zulfbungee.velocity.config;

import com.google.common.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import com.zulfen.zulfbungee.universal.config.ProxyConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class VelocityConfig extends ProxyConfig<ProxyServer, Player> {

    private final ConfigurationNode loadedConfig;

    public VelocityConfig(ZulfVelocityImpl instanceIn) {

        super(instanceIn);

        YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(
                instanceIn.getPluginFolder().resolve("config.yml")).build();

        try {
             this.loadedConfig = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public String getString(String node) {
        return loadedConfig.getNode(node).getString();
    }

    @Override
    public boolean getBoolean(String node) {
        return loadedConfig.getNode(node).getBoolean();
    }

    @Override
    public int getInt(String node) {
        return loadedConfig.getNode(node).getInt();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public List<Integer> getIntList(String node) {

        try {
            return loadedConfig.getNode(node).getList(TypeToken.of(Integer.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

}
