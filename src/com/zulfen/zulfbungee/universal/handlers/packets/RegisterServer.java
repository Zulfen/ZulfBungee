package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class RegisterServer<P, T> extends PacketHandler<P, T> {

    public RegisterServer(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        String serverName = (String) packetIn.getDataArray()[0];
        String stringAddress = (String) packetIn.getDataArray()[1];
        Integer port = (Integer) packetIn.getDataArray()[2];

        try {

            InetAddress inetAddr = InetAddress.getByName(stringAddress);
            InetSocketAddress socketAddress = new InetSocketAddress(inetAddr, port);
            getProxy().registerServer(serverName, socketAddress);

        } catch (UnknownHostException e) {
            getProxy().warning(String.format("Could not resolve address for registering a server!: (%s)", stringAddress));
            e.printStackTrace();
        }

        return null;

    }

}
