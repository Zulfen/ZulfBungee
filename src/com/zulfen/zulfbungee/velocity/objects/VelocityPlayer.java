package com.zulfen.zulfbungee.velocity.objects;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.velocity.ZulfVelocity;

import java.net.InetSocketAddress;
import java.util.Optional;

public class VelocityPlayer extends ZulfProxyPlayer<ProxyServer, Player> {

    private final Player velocityPlayer;

    private final ZulfVelocity zulfVelocity;
    private final ProxyServer velocity;

    public VelocityPlayer(Player velocityPlayerIn, ZulfProxyServer<ProxyServer, Player> serverIn, ZulfVelocity pluginIn) {
        super(pluginIn.getPlatform(), velocityPlayerIn.getUsername(), velocityPlayerIn.getUniqueId(), serverIn);
        this.zulfVelocity = pluginIn;
        this.velocity = zulfVelocity.getPlatform();
        this.velocityPlayer = velocityPlayerIn;
    }

    @Override
    public boolean hasPermission(String permission) {
        return velocityPlayer.hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        velocityPlayer.sendMessage(zulfVelocity.getLegacyTextSerializer().deserialize(message));
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return velocityPlayer.getVirtualHost();
    }

    @Override
    public void connect(ZulfProxyServer<ProxyServer, Player> serverIn) {
        Optional<RegisteredServer> server = velocity.getServer(serverIn.getName());
        server.ifPresent(registeredServer -> velocityPlayer.createConnectionRequest(registeredServer).connect());
    }

    @Override
    public void disconnect(String reason) {
        velocityPlayer.disconnect(zulfVelocity.getLegacyTextSerializer().deserialize(reason));
    }

}
