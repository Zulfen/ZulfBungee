package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyKick;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.stream.Stream;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        server.getPluginInstance().getTaskManager().newTask(() -> {

            ProxiedPlayer eventPlayer = event.getPlayer();

            String toName = event.getServer().getInfo().getName();

            server.getPluginInstance().logDebug("ServerConnected Player: " + eventPlayer.getName());
            server.getPluginInstance().logDebug("To server: " + event.getServer().getInfo().getName());

            if (eventPlayer.getServer() == null) {

                ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), proxyServer);

                server.getActiveConnections().get(toName).addPlayer(eventPlayer.getUniqueId(), playerOut);

                server.sendToAllClients(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                        playerOut));

            } else if (event.getServer() != null) {

                server.getPluginInstance().logDebug("From server: " + eventPlayer.getServer().getInfo().getName());

                String fromName = eventPlayer.getServer().getInfo().getName();

                server.getActiveConnections().get(fromName).removePlayer(eventPlayer.getUniqueId());

                ProxyServer serverOut = new ProxyServer(toName);
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), serverOut);

                server.getActiveConnections().get(toName).addPlayer(eventPlayer.getUniqueId(), playerOut);

                server.sendToAllClients(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true, playerOut));

            }

        }, event.toString());

    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        server.getPluginInstance().getTaskManager().newTask(() -> {

            ProxiedPlayer player = event.getPlayer();

            String serverName = event.getKickedFrom().getName();

            if (server.getActiveConnections().get(serverName) != null) {

                server.getActiveConnections().get(serverName).removePlayer(player.getUniqueId());

                ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId());


                String[] messages = Stream.of(event.getKickReasonComponent())
                        .map(BaseComponent::toLegacyText)
                        .toArray(String[]::new);


                server.sendToAllClients(new Packet(PacketTypes.KICK_EVENT, false, true,
                            new ProxyKick(messages, playerOut)));

            }

        }, event.toString());

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        server.getPluginInstance().getTaskManager().newTask(() -> {

            ProxiedPlayer player = event.getPlayer();

            if (player.getServer() != null) {

                String serverName = player.getServer().getInfo().getName();

                if (server.getActiveConnections().get(serverName) != null) {

                    server.getActiveConnections().get(serverName).removePlayer(player.getUniqueId());

                    server.sendToAllClients(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                            new ProxyPlayer(player.getName(), player.getUniqueId())));

                }

            }

        }, event.toString());

    }
}
