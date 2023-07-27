package com.zulfen.zulfbungee.bungeecord.command;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeConsole implements ProxyCommandSender<ProxyServer, ProxiedPlayer> {

    private final ProxyServer proxyServer;

    public BungeeConsole(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        proxyServer.getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                ('&', message)));
    }
}
