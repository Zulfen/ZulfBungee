package tk.zulfengaming.zulfbungee.universal.socket.packets;


import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ServerInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class ProxyClientInfo extends PacketHandler {

    public ProxyClientInfo(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        ServerInfo serverInfo = (ServerInfo) packetIn.getDataSingle();

        connection.setServerInfo(serverInfo);

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        InetAddress inetAddressIn = socketAddressIn.getAddress();
        int portIn = serverInfo.getMinecraftPort();

        for (Map.Entry<String, ServerInfo> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();

            int infoPort = infoSockAddr.getPort();

            InetAddress infoInetAddr = infoSockAddr.getAddress();

            if (infoInetAddr.equals(inetAddressIn) && portIn == infoPort) {

                String name = info.getKey();
                connection.setName(name);
                getMainServer().addActiveConnection(connection, name);

                return new Packet(PacketTypes.CONNECTION_NAME, false, true, name);

            }
        }

        return null;
    }
}