package com.zulfen.zulfbungee.bungeecord.objects;

import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Optional;

public class BungeePlayer extends ZulfProxyPlayer<ProxyServer, ProxiedPlayer> {

    private final ProxiedPlayer bungeePlayer;

    public BungeePlayer(ProxiedPlayer playerIn, BungeeServer serverIn) {
        super(net.md_5.bungee.api.ProxyServer.getInstance(), playerIn.getName(), playerIn.getUniqueId(), serverIn);
        this.bungeePlayer = playerIn;
    }

    private BaseComponent[] toComponent(String messageIn) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                ('&', messageIn));
    }

    @Override
    public void connect(ZulfProxyServer<ProxyServer, ProxiedPlayer> serverIn) {
        ServerInfo serverInfo = platform.getServerInfo(serverIn.getName());
        if (serverInfo != null) {
            bungeePlayer.connect(serverInfo);
        }
    }

    @Override
    public void disconnect(String reason) {
        bungeePlayer.disconnect(toComponent(reason));
    }

    @Override
    public boolean hasPermission(String permission) {
        return bungeePlayer.hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        bungeePlayer.sendMessage(toComponent(message));
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return Optional.of(bungeePlayer.getPendingConnection().getVirtualHost());
    }

}
