package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
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
    public void onServerSwitch(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        server.getPluginInstance().getTaskManager().newTask(() -> {

            String serverName = event.getFrom().getName();

            ServerConnection connection = server.getActiveConnections().get(serverName);
            HashMap<UUID, ProxyPlayer> players = connection.getPlayers();

            ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId());

            players.put(eventPlayer.getUniqueId(), playerOut);
            playerOut.setServer(new ProxyServer(serverName, players.values().toArray(new ProxyPlayer[0])));

            server.sendToAllClients(new Packet(PacketTypes.SWITCH_SERVER_EVENT, false, true, playerOut));

        }, event.toString());

    }

    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        for (ServerConnection connection : server.getActiveConnections().values()) {
            connection.getPlayers().remove(event.getPlayer().getUniqueId());
        }
    }
}
