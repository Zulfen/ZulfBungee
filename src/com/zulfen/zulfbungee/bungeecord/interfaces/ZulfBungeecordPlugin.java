package com.zulfen.zulfbungee.bungeecord.interfaces;

import com.zulfen.zulfbungee.universal.ZulfProxyPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class ZulfBungeecordPlugin extends ZulfProxyPlugin<ProxyServer, ProxiedPlayer> {

    public ZulfBungeecordPlugin(ProxyServer proxyServerIn, Plugin pluginInstance) {
        super(new ZulfBungeecordImpl(proxyServerIn, pluginInstance));
    }

}
