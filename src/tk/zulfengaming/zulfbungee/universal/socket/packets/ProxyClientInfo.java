package tk.zulfengaming.zulfbungee.universal.socket.packets;


import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class ProxyClientInfo<P> extends PacketHandler<P> {

    public ProxyClientInfo(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientInfo clientInfo = (ClientInfo) packetIn.getDataSingle();
        connection.setClientInfo(clientInfo);

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        InetAddress inetAddressIn = socketAddressIn.getAddress();
        int portIn = clientInfo.getMinecraftPort();

        for (Map.Entry<String, ZulfServerInfo<P>> info : getProxy().getServersCopy().entrySet()) {

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