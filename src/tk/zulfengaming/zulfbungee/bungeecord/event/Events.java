package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.task.TaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class Events implements Listener {

    private final MainServer mainServer;
    private final TaskManager taskManager;

    public Events(MainServer mainServerIn) {
        this.mainServer = mainServerIn;
        this.taskManager = mainServer.getPluginInstance().getTaskManager();
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());
            ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), proxyServer);

            mainServer.sendDirectToAllAsync(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                        playerOut));

        }

        if (eventPlayer.hasPermission("zulfen.admin") && eventPlayer.getServer() == null) {
            mainServer.getPluginInstance().checkUpdate(eventPlayer, false);
        }

    }

    @EventHandler
    public void onProxyPingEvent(ProxyPingEvent event) {

        PendingConnection connection = event.getConnection();
        ProxyPlayer playerOut = new ProxyPlayer(connection.getName(), connection.getUniqueId());

        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.PROXY_PLAYER_PING, false, true,
                new ProxyPlayerDataContainer(connection.getSocketAddress().toString(), playerOut)));

    }

    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            String toName = eventPlayer.getServer().getInfo().getName();

            ProxyServer serverOut = new ProxyServer(toName);
            ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), serverOut);

            mainServer.sendDirectToAllAsync(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true, playerOut));

        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        if (event.getCause() == ServerKickEvent.Cause.SERVER) {

            ProxiedPlayer player = event.getPlayer();

            String serverName = event.getKickedFrom().getName();

            if (mainServer.getServerNames().contains(serverName)) {

                ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId());

                String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());

                mainServer.sendDirectToAllAsync(new Packet(PacketTypes.KICK_EVENT, false, true,
                        new ProxyPlayerDataContainer(legacyText, playerOut)));

            }

        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            String serverName = player.getServer().getInfo().getName();

            if (mainServer.getServerNames().contains(serverName)) {

                mainServer.sendDirectToAllAsync(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                        new ProxyPlayer(player.getName(), player.getUniqueId())));

            }


        }

    }
}
