package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class Events implements Listener {

    private final MainServer mainServer;

    public Events(MainServer mainServerIn) {
        this.mainServer = mainServerIn;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            mainServer.getPluginInstance().getTaskManager().newTask(() -> {

                ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), proxyServer);

                mainServer.sendToAllClients(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                        playerOut));

            });

        }

        if (eventPlayer.hasPermission("zulfen.admin") && eventPlayer.getServer() == null) {
            mainServer.getPluginInstance().checkUpdate(eventPlayer, false);
        }

    }

    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            mainServer.getPluginInstance().getTaskManager().newTask(() -> {

                String toName = eventPlayer.getServer().getInfo().getName();

                ProxyServer serverOut = new ProxyServer(toName);
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), serverOut);

                mainServer.sendToAllClients(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true, playerOut));

            });

        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        if (event.getCause() == ServerKickEvent.Cause.SERVER) {

            mainServer.getPluginInstance().getTaskManager().newTask(() -> {

                ProxiedPlayer player = event.getPlayer();

                String serverName = event.getKickedFrom().getName();

                if (mainServer.getServerNames().contains(serverName)) {

                    ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId());

                    String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());

                    mainServer.sendToAllClients(new Packet(PacketTypes.KICK_EVENT, false, true,
                            new ProxyPlayerDataContainer(legacyText, new ProxyPlayer[]{playerOut})));

                }

            });

        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            mainServer.getPluginInstance().getTaskManager().newTask(() -> {

                String serverName = player.getServer().getInfo().getName();

                if (mainServer.getServerNames().contains(serverName)) {

                    mainServer.sendToAllClients(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                            new ProxyPlayer(player.getName(), player.getUniqueId())));

                }

            });

        }

    }
}
