package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;


import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class ProxyClientInfo extends PacketHandler {

    public ProxyClientInfo(Server serverIn) {
        super(serverIn, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        ClientInfo clientInfo = (ClientInfo) packetIn.getDataSingle();

        // potentially update this atomically.
        connection.setClientInfo(clientInfo);

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        InetAddress inetAddressIn = socketAddressIn.getAddress();
        int portIn = clientInfo.getMinecraftPort();

        for (Map.Entry<String, net.md_5.bungee.api.config.ServerInfo> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();

            int infoPort = infoSockAddr.getPort();

            InetAddress infoInetAddr = infoSockAddr.getAddress();

            if (infoInetAddr.equals(inetAddressIn) && portIn == infoPort) {

                String name = info.getKey();
                getMainServer().addActiveConnection(connection, name);

                return new Packet(PacketTypes.CONNECTION_NAME, false, true, name);

            }
        }

        return null;
    }
}