package com.zulfen.zulfbungee.universal.handlers.packets;


import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.net.InetSocketAddress;
import java.util.Map;

public class ProxyClientInfo<P, T> extends PacketHandler<P, T> {

    public ProxyClientInfo(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientInfo clientInfo = (ClientInfo) packetIn.getDataSingle();
        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        // Returns a map of
        for (Map.Entry<String, ZulfProxyServer<P, T>> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();
            String name = info.getKey();

            if (!getMainServer().getActiveServerNames().contains(name)) {

                // new server
                boolean portCheck = infoSockAddr.getPort() == clientInfo.getMinecraftPort();
                boolean addressCheck = infoSockAddr.getAddress().equals(socketAddressIn.getAddress());

                if (addressCheck && portCheck) {

                    getMainServer().addActiveConnection(connection, name, clientInfo);
                    return new Packet(PacketTypes.CONNECTION_NAME, true, true, name);

                } else {

                    getProxy().warning(String.format("ZulfBungee couldn't find the client with the address %s!", socketAddressIn.getAddress()));
                    getProxy().warning("Please make sure that the address in your proxy's main config is valid!");
                    getProxy().warning(String.format("Address check returned %s (%s compared to %s)", false, infoSockAddr.getAddress(), socketAddressIn.getAddress()));
                    getProxy().warning(String.format("Port check returned %s (%s compared to %s)", portCheck, infoSockAddr.getPort(), clientInfo.getMinecraftPort()));
                    getProxy().warning("If you are sure this server is in your config, either provide a connection name or register it with ZulfBungee syntax and restart that server.");
                    return new Packet(PacketTypes.INVALID_CONFIGURATION, true, true);

                }

            }


        }

        getProxy().warning(String.format("ZulfBungee couldn't find the server with address %s in the proxy's config!", connection.getAddress()));
        getProxy().warning("Either provide a connection name, or register it with ZulfBungee syntax and restart that server.");
        return new Packet(PacketTypes.INVALID_CONFIGURATION, true, true);

    }
}