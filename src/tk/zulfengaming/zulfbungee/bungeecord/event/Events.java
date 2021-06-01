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

import java.util.ArrayList;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {

        ProxiedPlayer player = event.getPlayer();

        server.getPluginInstance().getTaskManager().newTask(() -> {

            String serverName = event.getFrom().getName();

            ServerConnection connection = server.getActiveConnections().get(serverName);

            if (connection != null) {

                ProxyServer proxyServer = server.getServers().get(connection);

                ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId(), proxyServer);

                proxyServer.addPlayer(playerOut);

                server.sendToAllClients(new Packet(PacketTypes.SWITCH_SERVER_EVENT, false, true, playerOut));
            }

        }, event.toString());

    }

    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();
        ProxyPlayer proxyPlayer = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId());

        for (ProxyServer server : server.getServers().values()) {

            ArrayList<ProxyPlayer> players = server.getPlayers();

            players.removeIf(player -> player.equals(proxyPlayer));

        }
    }
}
