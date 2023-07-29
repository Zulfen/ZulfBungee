package com.zulfen.zulfbungee.spigot.event;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerKick;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import com.zulfen.zulfbungee.spigot.event.events.EventProxyMessage;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.connections.ChannelConnectionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListeners implements Listener {

    private final ZulfBungeeSpigot pluginInstance;

    public EventListeners(ZulfBungeeSpigot pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    @EventHandler
    public void onChannelEvent(PlayerChannelEvent playerChannelEvent) {
        if (playerChannelEvent.getChannel().equals("zproxy:channel")) {
            ConnectionManager<?> connectionManager = pluginInstance.getConnectionManager();
            if (connectionManager instanceof ChannelConnectionManager) {
                ChannelConnectionManager channelConnectionManager = (ChannelConnectionManager) connectionManager;
                if (pluginInstance.getServer().getOnlinePlayers().size() == 1) {
                    channelConnectionManager.signalAvailableConnection();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerDisconnectEvent) {
        ConnectionManager<?> connectionManager = pluginInstance.getConnectionManager();
        if (connectionManager instanceof ChannelConnectionManager) {
            ChannelConnectionManager channelConnectionManager = (ChannelConnectionManager) connectionManager;
            if (pluginInstance.getServer().getOnlinePlayers().size() == 1) {
                channelConnectionManager.newChannelConnection();
            }
        }
    }

    @EventHandler
    public void onPlayerSwitchServer(EventPlayerSwitchServer event) {}

    @EventHandler
    public void onServerMessage(EventProxyMessage event) {}

    @EventHandler
    public void onPlayerDisconnect(EventPlayerServerDisconnect event) {}

    @EventHandler
    public void onPlayerKick(EventPlayerServerKick event) {}


}
