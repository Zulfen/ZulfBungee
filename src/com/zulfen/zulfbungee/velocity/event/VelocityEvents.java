package com.zulfen.zulfbungee.velocity.event;

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
import com.zulfen.zulfbungee.universal.event.ProxyEvents;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.velocity.ZulfVelocity;
import com.zulfen.zulfbungee.velocity.objects.VelocityPlayer;
import com.zulfen.zulfbungee.velocity.objects.VelocityServer;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class VelocityEvents extends ProxyEvents<ProxyServer, Player> {

    private final ZulfVelocity zulfVelocity;

    public VelocityEvents(MainServer<ProxyServer, Player> mainServerIn) {
        super(mainServerIn);
        this.zulfVelocity = (ZulfVelocity) mainServerIn.getPluginInstance();
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent serverConnectedEvent) {

        RegisteredServer server = serverConnectedEvent.getServer();
        VelocityServer velocityServer = new VelocityServer(server, zulfVelocity);

        Player eventPlayer = serverConnectedEvent.getPlayer();
        VelocityPlayer velocityPlayer = new VelocityPlayer(eventPlayer, velocityServer, zulfVelocity);

        serverConnected(velocityPlayer);

    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent kickedFromServerEvent) {

        Player velocityPlayer = kickedFromServerEvent.getPlayer();
        Optional<Component> optionalReason = kickedFromServerEvent.getServerKickReason();

        if (optionalReason.isPresent()) {
            serverKick(velocityPlayer.getUsername(), velocityPlayer.getUniqueId(),
                    zulfVelocity.getLegacyTextSerializer().serialize(optionalReason.get()));
        } else {
            serverKick(velocityPlayer.getUsername(), velocityPlayer.getUniqueId(), "");
        }


    }

    @Subscribe
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        Player velocityPlayer = disconnectEvent.getPlayer();
        Optional<ServerConnection> serverOptional = velocityPlayer.getCurrentServer();
        serverOptional.ifPresent(serverConnection -> serverDisconnect(velocityPlayer.getUsername(), velocityPlayer.getUniqueId(),
                serverConnection.getServerInfo().getName()));

    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {

        if (event.getIdentifier().equals(MinecraftChannelIdentifier.from("zproxy:channel"))) {

            event.setResult(PluginMessageEvent.ForwardResult.handled());
            ChannelMessageSource source = event.getSource();

            String serverName;
            if (source instanceof ServerConnection) {
                ServerConnection serverConnection = (ServerConnection) source;
                serverName = serverConnection.getServerInfo().getName();
            } else {
                return;
            }

            pluginMessage(serverName, event.getData());

        }

    }

}
