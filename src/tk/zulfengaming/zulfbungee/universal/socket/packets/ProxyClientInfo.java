package tk.zulfengaming.zulfbungee.universal.socket.packets;


import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
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

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        // Returns a map of
        for (Map.Entry<String, ZulfServerInfo<P>> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();

            boolean portCheck = infoSockAddr.getPort() == clientInfo.getMinecraftPort();
            boolean addressCheck = infoSockAddr.getAddress().equals(socketAddressIn.getAddress());

            if (addressCheck && portCheck) {

                String name = info.getKey();

                if (!getMainServer().getServerNames().contains(name)) {
                    getMainServer().addActiveConnection(connection, name, clientInfo);
                    return new Packet(PacketTypes.CONNECTION_NAME, false, true, name);
                }


            } else if (!addressCheck) {

                getProxy().warning(String.format("We couldn't find the client with the address %s!", socketAddressIn.getAddress()));
                getProxy().warning("Please make sure that the address in your proxy's main config is valid!");
                getProxy().warning(String.format("Address check returned %s (%s compared to %s)", false, infoSockAddr.getAddress(), socketAddressIn.getAddress()));
                getProxy().warning(String.format("Port check returned %s (%s compared to %s)", portCheck, infoSockAddr.getPort(), clientInfo.getMinecraftPort()));

                connection.sendDirect(new Packet(PacketTypes.INVALID_CONFIGURATION, false, true, new Object[0]));
                break;

            }

        }

        return null;
    }
}