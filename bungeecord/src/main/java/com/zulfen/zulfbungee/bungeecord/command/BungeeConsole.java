package com.zulfen.zulfbungee.bungeecord.command;

import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeConsole implements ProxyCommandSender {

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
        proxyServer.getConsole().sendMessage(TextComponent.fromLegacy(ChatColor.translateAlternateColorCodes
                ('&', message)));
    }
}
