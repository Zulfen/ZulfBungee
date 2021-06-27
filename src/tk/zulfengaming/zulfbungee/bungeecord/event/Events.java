package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyKick;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        server.getPluginInstance().getTaskManager().newTask(() -> {

            if (eventPlayer.getServer() == null) {

                ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), proxyServer);

                server.sendToAllClients(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                        playerOut));

            }

        }, event.toString());

    }

    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        server.getPluginInstance().getTaskManager().newTask(() -> {

            if (event.getFrom() != null) {

                String toName = eventPlayer.getServer().getInfo().getName();

                ProxyServer serverOut = new ProxyServer(toName);
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), serverOut);


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

                ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId());


                List<String> list = new ArrayList<>();
                for (BaseComponent component : event.getKickReasonComponent()) {
                    String toLegacyText = component.toLegacyText();
                    list.add(toLegacyText);
                }

                String[] messages = list.toArray(new String[0]);


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

                    server.sendToAllClients(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                            new ProxyPlayer(player.getName(), player.getUniqueId())));

                }

            }

        }, event.toString());

    }
}
