package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.chat.TextComponent;
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
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), proxyServer);

                server.sendToAllClients(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                        playerOut));

            });

        }

        if (eventPlayer.hasPermission("zulfen.admin") && eventPlayer.getServer() == null) {
            server.getPluginInstance().checkUpdate(eventPlayer);
        }

    }

    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                String toName = eventPlayer.getServer().getInfo().getName();

                ProxyServer serverOut = new ProxyServer(toName);
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), serverOut);

                server.sendToAllClients(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true, playerOut));

            });

        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        if (event.getCause() == ServerKickEvent.Cause.SERVER) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                ProxiedPlayer player = event.getPlayer();

                String serverName = event.getKickedFrom().getName();

                if (server.getServerNames().contains(serverName)) {

                    ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId());

                    String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());

                    server.sendToAllClients(new Packet(PacketTypes.KICK_EVENT, false, true,
                            new ProxyPlayerDataContainer(legacyText, new ProxyPlayer[]{playerOut})));

                }

            });

        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                String serverName = player.getServer().getInfo().getName();

                if (server.getServerNames().contains(serverName)) {

                    server.sendToAllClients(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                            new ProxyPlayer(player.getName(), player.getUniqueId())));

                }

            });

        }

    }
}
