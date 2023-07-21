package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeeServer;
import tk.zulfengaming.zulfbungee.universal.event.ProxyEvents;
import tk.zulfengaming.zulfbungee.universal.interfaces.NativePlayerConverter;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Optional;

public class BungeeEvents extends ProxyEvents<ProxyServer, ProxiedPlayer> implements Listener {

    private final NativePlayerConverter<ProxiedPlayer, ProxyServer> playerConverter;

    public BungeeEvents(MainServer<ProxyServer, ProxiedPlayer> mainServerIn) {
        super(mainServerIn);
         this.playerConverter = mainServer.getPluginInstance().getPlayerConverter();
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            ServerInfo serverInfo = event.getServer().getInfo();
            Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(serverInfo.getName());

            ZulfProxyPlayer<ProxyServer, ProxiedPlayer> bungeePlayer = new BungeePlayer(eventPlayer,
                    new BungeeServer(serverInfo));

            getClientInfo.ifPresent(info -> serverConnected(new ClientServer(serverInfo.getName(), info), bungeePlayer));

        }
    }


    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            ServerInfo serverInfo = eventPlayer.getServer().getInfo();

            Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(serverInfo.getName());

            Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> proxyPlayerOptional = playerConverter.apply(eventPlayer);

            proxyPlayerOptional.ifPresent(optionalPlayer
                    -> getClientInfo.ifPresent(info
                    -> serverConnected(new ClientServer(serverInfo.getName(), info), optionalPlayer)));


        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        ProxiedPlayer player = event.getPlayer();

        String serverName = event.getKickedFrom().getName();

        if (mainServer.getServerNames().contains(serverName)) {

            String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());
            serverKick(new ClientPlayer(player.getName(), player.getUniqueId()), legacyText);

        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            String serverName = player.getServer().getInfo().getName();

            if (mainServer.getServerNames().contains(serverName)) {

                serverDisconnect(new ClientPlayer(player.getName(), player.getUniqueId()));

            }

        }

    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {

        if (event.getTag().equals("zproxy:channel")) {

            String serverName;
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                serverName = receiver.getServer().getInfo().getName();
            } else if (event.getReceiver() instanceof Server) {
                Server receiver = (Server) event.getReceiver();
                serverName = receiver.getInfo().getName();
            } else {
                return;
            }

            pluginMessage(serverName, event.getData());

        }
    }

}
