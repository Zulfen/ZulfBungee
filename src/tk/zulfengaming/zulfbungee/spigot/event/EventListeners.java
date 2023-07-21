package tk.zulfengaming.zulfbungee.spigot.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerKick;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventProxyMessage;
import tk.zulfengaming.zulfbungee.spigot.managers.ChannelConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;

public class EventListeners implements Listener {

    private final ZulfBungeeSpigot pluginInstance;

    public EventListeners(ZulfBungeeSpigot pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    @EventHandler
    public void onPlayerChannelEvent(PlayerChannelEvent playerChannelEvent) {
        if (playerChannelEvent.getChannel().equals("zproxy:channel")) {
            if (pluginInstance.getConnectionManager() instanceof ChannelConnectionManager) {
                if (pluginInstance.getServer().getOnlinePlayers().size() == 1) {
                    pluginInstance.releaseChannelWait();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerDisconnectEvent) {
        ConnectionManager connectionManager = pluginInstance.getConnectionManager();
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
