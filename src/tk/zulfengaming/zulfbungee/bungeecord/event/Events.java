package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.HashMap;
import java.util.UUID;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        server.getPluginInstance().getTaskManager().newTask(() -> {

            ProxiedPlayer eventPlayer = event.getPlayer();

            ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());

            server.sendToAllClients(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                    new ProxyPlayer(eventPlayer.getName(), event.getPlayer().getUniqueId(), proxyServer)));

        }, event.toString());

    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        ServerInfo fromServer = event.getFrom();
        net.md_5.bungee.api.connection.Server toServer = eventPlayer.getServer();

        server.getPluginInstance().getTaskManager().newTask(() -> {

            ProxyPlayer playerOut;

            if (fromServer != null) {

                String fromServerName = fromServer.getName();

                HashMap<UUID, ProxyPlayer> fromPlayers = server.getActiveConnections().get(fromServerName).getPlayers();

                fromPlayers.remove(eventPlayer.getUniqueId());

            }

            String toServerName = toServer.getInfo().getName();

            ServerConnection connection = server.getActiveConnections().get(toServerName);
            HashMap<UUID, ProxyPlayer> toPlayers = connection.getPlayers();

            playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId());

            toPlayers.put(eventPlayer.getUniqueId(), playerOut);

            playerOut.setServer(new ProxyServer(toServerName));

            server.sendToAllClients(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true, playerOut));

        }, event.toString());

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        server.getPluginInstance().getTaskManager().newTask(() -> {

            ProxiedPlayer player = event.getPlayer();

            for (ServerConnection connection : server.getActiveConnections().values()) {
                connection.getPlayers().remove(player.getUniqueId());
            }

            server.sendToAllClients(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                    new ProxyPlayer(player.getName(), player.getUniqueId())));

        }, event.toString());

    }
}
