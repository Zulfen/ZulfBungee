package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;


import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.ClientUpdateData;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class ClientHandshake extends PacketHandler {

    public ClientHandshake(Server serverIn) {
        super(serverIn, PacketTypes.CLIENT_INFO);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        ServerInfo serverInfo = (ServerInfo) packetIn.getDataSingle();

        // potentially update this atomically.
        connection.setClientInfo(serverInfo);

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        InetAddress inetAddressIn = socketAddressIn.getAddress();
        int portIn = serverInfo.getMinecraftPort();

        for (Map.Entry<String, net.md_5.bungee.api.config.ServerInfo> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();

            int infoPort = infoSockAddr.getPort();

            InetAddress infoInetAddr = infoSockAddr.getAddress();

            if (infoInetAddr.equals(inetAddressIn) && portIn == infoPort) {

                String name = info.getKey();
                getMainServer().addActiveConnection(connection, name);

                String[] scriptNames = getMainServer().getPluginInstance().getConfig()
                        .getAvailableScripts().toArray(new String[0]);

                return new Packet(PacketTypes.CLIENT_UPDATE, false, true, new ClientUpdateData(name, scriptNames));

            }
        }

        return null;
    }
}