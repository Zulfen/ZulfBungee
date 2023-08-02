package com.zulfen.zulfbungee.bungeecord.event;

import com.zulfen.zulfbungee.bungeecord.ZulfBungeecord;
import com.zulfen.zulfbungee.bungeecord.objects.BungeePlayer;
import com.zulfen.zulfbungee.bungeecord.objects.BungeeServer;
import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;
import com.zulfen.zulfbungee.universal.event.ProxyEvents;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeEvents extends ProxyEvents<ProxyServer, ProxiedPlayer> implements Listener {

    private final ZulfBungeecord bungeecordInstance;

    public BungeeEvents(MainServer<ProxyServer, ProxiedPlayer> mainServerIn) {
        super(mainServerIn);
        ZulfBungeeProxy<ProxyServer, ProxiedPlayer> pluginInstance = mainServer.getPluginInstance();
        if (pluginInstance instanceof ZulfBungeecord) {
            this.bungeecordInstance = (ZulfBungeecord) pluginInstance;
        } else {
            throw new RuntimeException("Tried to instantiate BungeeEvents, but the plugin instance is reported as not being Bungeecord.");
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            ServerInfo serverInfo = event.getServer().getInfo();
            BungeePlayer bungeePlayer = new BungeePlayer(eventPlayer, new BungeeServer(serverInfo));
            serverConnected(bungeePlayer);

        }
    }


    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            ServerInfo from = event.getFrom();
            ServerInfo to = eventPlayer.getServer().getInfo();

            switchServer(to.getName(), from.getName(), eventPlayer.getName(), eventPlayer.getUniqueId());

        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        ProxiedPlayer player = event.getPlayer();
        String serverName = event.getKickedFrom().getName();

        if (bungeecordInstance.isWaterfall()) {
            if (event.getCause() == ServerKickEvent.Cause.LOST_CONNECTION) {
                return;
            }
        }

        if (mainServer.getServerNames().contains(serverName)) {
            String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());
            serverKick(player.getName(), player.getUniqueId(), legacyText);
        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            String serverName = player.getServer().getInfo().getName();

            if (mainServer.getServerNames().contains(serverName)) {
                serverDisconnect(player.getName(), player.getUniqueId(), serverName);
            }

        }

    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {

        if (event.getTag().equals("zproxy:channel")) {

            Connection sender = event.getSender();

            String serverName;
            if (sender instanceof Server) {
                Server server = (Server) sender;
                serverName = server.getInfo().getName();
            } else {
                return;
            }

            pluginMessage(serverName, event.getData());

        }
    }

}
