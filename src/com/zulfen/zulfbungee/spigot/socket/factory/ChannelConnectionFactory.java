package com.zulfen.zulfbungee.spigot.socket.factory;

import com.zulfen.zulfbungee.spigot.interfaces.ConnectionFactory;
import com.zulfen.zulfbungee.spigot.managers.connections.ChannelConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.ClientChannelConnection;

import java.net.SocketAddress;

public class ChannelConnectionFactory extends ConnectionFactory<ClientChannelConnection, ChannelConnectionManager> {

    private SocketAddress socketAddress;
    private int compressPackets;

    public ChannelConnectionFactory(ChannelConnectionManager connectionManagerIn) {
        super(connectionManagerIn);
    }

    public ChannelConnectionFactory withAddress(SocketAddress socketAddressIn) {
        socketAddress = socketAddressIn;
        return this;
    }

    public ChannelConnectionFactory compressLargePacketTo(int compressPacketsIn) {
        compressPackets = compressPacketsIn;
        return this;
    }

    @Override
    public ClientChannelConnection build() {
        return new ClientChannelConnection(connectionManager, socketAddress, compressPackets);
    }

}
