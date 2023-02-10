package tk.zulfengaming.zulfbungee.universal.socket.packets;


import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;

import java.net.InetSocketAddress;
import java.util.Map;

public class ProxyClientInfo<P> extends PacketHandler<P> {

    public ProxyClientInfo(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientInfo clientInfo = (ClientInfo) packetIn.getDataSingle();
        connection.setClientInfo(clientInfo);

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        // Returns a map of
        for (Map.Entry<String, ZulfServerInfo<P>> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();

            boolean portCheck = infoSockAddr.getPort() == clientInfo.getMinecraftPort();

            if ((infoSockAddr.getAddress().equals(socketAddressIn.getAddress()) && portCheck)) {

                String name = info.getKey();

                if (!getMainServer().getServerNames().contains(name)) {
                    getMainServer().addActiveConnection(connection, name);
                }

                return new Packet(PacketTypes.CONNECTION_NAME, false, true, name);

            }

        }

        return null;
    }
}