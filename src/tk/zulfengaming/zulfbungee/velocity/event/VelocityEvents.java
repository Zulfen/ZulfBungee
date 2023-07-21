package tk.zulfengaming.zulfbungee.velocity.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import tk.zulfengaming.zulfbungee.universal.event.ProxyEvents;
import tk.zulfengaming.zulfbungee.universal.interfaces.NativePlayerConverter;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

import java.util.Optional;

public class VelocityEvents extends ProxyEvents<ProxyServer, Player> {

    private final ZulfVelocity zulfVelocity;
    private final NativePlayerConverter<Player, ProxyServer> playerConverter;

    public VelocityEvents(MainServer<ProxyServer, Player> mainServerIn) {
        super(mainServerIn);
        this.zulfVelocity = (ZulfVelocity) mainServerIn.getPluginInstance();
        this.playerConverter = zulfVelocity.getPlayerConverter();
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent serverConnectedEvent) {

        RegisteredServer server;

        if (!serverConnectedEvent.getPreviousServer().isPresent()) {
            server = serverConnectedEvent.getServer();
        } else {
            server = serverConnectedEvent.getPreviousServer().get();
        }

        String serverName = server.getServerInfo().getName();

        Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(serverName);

        Player eventPlayer = serverConnectedEvent.getPlayer();
        Optional<ZulfProxyPlayer<ProxyServer, Player>> proxyPlayerOptional = playerConverter.apply(eventPlayer);

        getClientInfo.ifPresent(info -> proxyPlayerOptional.
                ifPresent(proxyPlayer ->
                        serverConnected(new ClientServer(serverName, info), proxyPlayer)));

    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent kickedFromServerEvent) {

        Player velocityPlayer = kickedFromServerEvent.getPlayer();
        ClientPlayer playerOut = new ClientPlayer(velocityPlayer.getUsername(), velocityPlayer.getUniqueId());

        if (kickedFromServerEvent.getServerKickReason().isPresent()) {
            serverKick(playerOut, zulfVelocity.getLegacyTextSerializer()
                    .serialize(kickedFromServerEvent.getServerKickReason().get()));
        } else {
            serverKick(playerOut, "");
        }

    }

    @Subscribe
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        Player velocityPlayer = disconnectEvent.getPlayer();
        serverDisconnect(new ClientPlayer(velocityPlayer.getUsername(), velocityPlayer.getUniqueId()));
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {

        if (event.getIdentifier().equals(MinecraftChannelIdentifier.from("zproxy:channel"))) {

            event.setResult(PluginMessageEvent.ForwardResult.handled());
            ChannelMessageSource source = event.getSource();

            String serverName;
            if (source instanceof Player) {
                Player player = (Player) source;
                Optional<ServerConnection> serverOptional = player.getCurrentServer();
                if (serverOptional.isPresent()) {
                    serverName = serverOptional.get().getServerInfo().getName();
                } else {
                    return;
                }
            } else if (source instanceof ServerConnection) {
                ServerConnection serverConnection = (ServerConnection) source;
                serverName = serverConnection.getServerInfo().getName();
            } else {
                return;
            }

            pluginMessage(serverName, event.getData());

        }

    }

}
