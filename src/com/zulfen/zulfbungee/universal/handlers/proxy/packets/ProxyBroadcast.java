package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientServerDataContainer;

import java.util.Optional;

public class ProxyBroadcast<P, T> extends PacketHandler<P, T> {

    public ProxyBroadcast(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientServerDataContainer dataContainer = (ClientServerDataContainer) packetIn.getDataSingle();
        ClientServer[] servers = dataContainer.getServers();

        String message = (String) dataContainer.getData();

        if (servers.length > 0) {
            for (ClientServer server : servers) {
                Optional<ZulfProxyServer<P, T>> proxyServer = getProxy().getServer(server);
                proxyServer.ifPresent(zulfProxyServer -> getProxy().broadcast(message, zulfProxyServer));

            }
        } else {
            getProxy().broadcast(message);
        }

        return null;

    }
}