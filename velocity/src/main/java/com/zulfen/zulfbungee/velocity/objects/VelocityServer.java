package com.zulfen.zulfbungee.velocity.objects;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.spongepowered.configurate.ConfigurationNode;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;

import java.util.List;
import java.util.stream.Collectors;

public class VelocityServer extends ZulfProxyServer<ProxyServer, Player, ConfigurationNode> {

    private final RegisteredServer server;
    private final ZulfVelocityImpl pluginInstance;

    public VelocityServer(RegisteredServer velocityServerIn, ZulfVelocityImpl pluginIn) {
        super(velocityServerIn.getServerInfo().getName(), velocityServerIn.getServerInfo().getAddress());
        this.server = velocityServerIn;
        this.pluginInstance = pluginIn;
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer, Player, ConfigurationNode>> getPlayers() {
        return server.getPlayersConnected().stream()
                .map(player -> new VelocityPlayer(player, this, pluginInstance))
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendData(String channelNameIn, byte[] dataOut) {
        return server.sendPluginMessage(MinecraftChannelIdentifier.from(channelNameIn), dataOut);
    }


}
