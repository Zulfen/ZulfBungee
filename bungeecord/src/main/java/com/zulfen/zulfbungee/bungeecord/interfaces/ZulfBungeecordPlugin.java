package com.zulfen.zulfbungee.bungeecord.interfaces;

import com.zulfen.zulfbungee.core.ZulfProxyPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class ZulfBungeecordPlugin extends ZulfProxyPlugin<ProxyServer, ProxiedPlayer, Configuration> {

    public ZulfBungeecordPlugin(ProxyServer proxyServerIn, Plugin pluginInstance) {
        super(new ZulfBungeecordImpl(proxyServerIn, pluginInstance));
    }

}
