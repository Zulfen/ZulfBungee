package com.zulfen.zulfbungee.velocity.interfaces;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.universal.ZulfProxyPlugin;
import com.zulfen.zulfbungee.velocity.ZulfVelocityMain;
import org.slf4j.Logger;

import java.nio.file.Path;

public class ZulfVelocityPlugin extends ZulfProxyPlugin<ProxyServer, Player> {

    public ZulfVelocityPlugin(ProxyServer proxyServerIn, ZulfVelocityMain pluginInstance, Logger loggerIn, Path dataDirectoryIn, String versionIn) {
        super(new ZulfVelocityImpl(proxyServerIn, pluginInstance, loggerIn, dataDirectoryIn, versionIn));
    }

}
