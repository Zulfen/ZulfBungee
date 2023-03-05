package tk.zulfengaming.zulfbungee.velocity.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import tk.zulfengaming.zulfbungee.universal.event.ProxyEvents;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;
import tk.zulfengaming.zulfbungee.velocity.objects.VelocityPlayer;

import java.util.Optional;

public class VelocityEvents extends ProxyEvents<ProxyServer> {

    private final ZulfVelocity zulfVelocity;

    public VelocityEvents(MainServer<ProxyServer> mainServerIn) {
        super(mainServerIn);
        this.zulfVelocity = (ZulfVelocity) mainServerIn.getPluginInstance();
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent serverConnectedEvent) {

        if (!serverConnectedEvent.getPreviousServer().isPresent()) {

            String serverName = serverConnectedEvent.getServer().getServerInfo().getName();
            Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(serverName);

            getClientInfo.ifPresent(clientInfo -> serverConnected(new ClientServer(serverName, clientInfo), new VelocityPlayer(serverConnectedEvent.getPlayer(),
                    zulfVelocity)));


        } else {

            Optional<RegisteredServer> previousServer = serverConnectedEvent.getPreviousServer();
            String serverName = previousServer.get().getServerInfo().getName();

            Optional<ClientInfo> getClientInfo = mainServer.getClientInfo(serverName);

            getClientInfo.ifPresent(clientInfo -> switchServer(new ClientServer(serverName, clientInfo), new VelocityPlayer(serverConnectedEvent.getPlayer(),
                    zulfVelocity)));


        }

    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent kickedFromServerEvent) {

        Player velocityPlayer = kickedFromServerEvent.getPlayer();
        ClientPlayer playerOut = new ClientPlayer(velocityPlayer.getUsername(), velocityPlayer.getUniqueId());

        if (kickedFromServerEvent.getServerKickReason().isPresent()) {
            serverKick(playerOut, zulfVelocity.getLegacyTextSerialiser()
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

}
