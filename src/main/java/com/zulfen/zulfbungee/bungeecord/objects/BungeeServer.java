package com.zulfen.zulfbungee.bungeecord.objects;

import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.stream.Collectors;

public class BungeeServer extends ZulfProxyServer<ProxyServer, ProxiedPlayer, Configuration> {

    private final ServerInfo serverInfo;

    public BungeeServer(ServerInfo serverInfoIn) {
        super(serverInfoIn.getName(), serverInfoIn.getSocketAddress());
        this.serverInfo = serverInfoIn;
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer, ProxiedPlayer, Configuration>> getPlayers() {
        return serverInfo.getPlayers().stream()
                .map(proxiedPlayer -> new BungeePlayer(proxiedPlayer, this))
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendData(String channelNameIn, byte[] dataOut) {
        return serverInfo.sendData(channelNameIn, dataOut, false);
    }

}
