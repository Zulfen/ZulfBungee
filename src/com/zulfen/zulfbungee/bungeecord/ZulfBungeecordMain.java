package com.zulfen.zulfbungee.bungeecord;

import com.zulfen.zulfbungee.bungeecord.command.BungeeCommand;
import com.zulfen.zulfbungee.bungeecord.event.BungeeEvents;
import com.zulfen.zulfbungee.bungeecord.interfaces.ZulfBungeecordPlugin;
import com.zulfen.zulfbungee.universal.managers.CommandHandlerManager;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public class ZulfBungeecordMain extends Plugin {

    private ZulfBungeecordPlugin plugin;
    private MainServer<ProxyServer, ProxiedPlayer> mainServer;

    @Override
    public void onEnable() {
        plugin = new ZulfBungeecordPlugin(getProxy(), this);
        mainServer = plugin.getMainServer();
        getProxy().getPluginManager().registerListener(this, new BungeeEvents(mainServer));
        getProxy().getPluginManager().registerCommand(this, new BungeeCommand(new CommandHandlerManager<>(mainServer)));
    }

    @Override
    public void onDisable() {
        try {
            mainServer.end();
            plugin.getTaskManager().shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
