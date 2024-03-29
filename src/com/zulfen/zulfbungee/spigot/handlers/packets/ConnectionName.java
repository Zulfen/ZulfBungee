package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Optional;

public class ConnectionName extends PacketHandler {

    public ConnectionName(ClientConnection<?> connectionIn) {
        super(connectionIn, false, PacketTypes.CONNECTION_NAME);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        String name = (String) packetIn.getDataSingle();
        ConnectionManager<?> connectionManager = getConnection().getPluginInstance().getConnectionManager();

        Optional<ClientServer> proxyServer = connectionManager.getProxyServer(name);
        proxyServer.ifPresent(connectionManager::setThisServer);

        getConnection().signifyProperConnection();

    }
}
