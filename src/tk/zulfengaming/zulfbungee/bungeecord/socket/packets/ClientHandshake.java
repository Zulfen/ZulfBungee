package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ClientInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

public class ClientHandshake extends PacketHandler {

    public ClientHandshake(Server serverIn) {
        super(serverIn, PacketTypes.CLIENT_HANDSHAKE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ServerConnection connection = getMainServer().getSocketConnections().get(address);

        connection.setClientInfo((ClientInfo) packetIn.getDataSingle());

        InetAddress addressIn = ((InetSocketAddress) address).getAddress();

        for (Map.Entry<String, ServerInfo> info : getProxy().getServersCopy().entrySet()) {

            SocketAddress infoSockAddr = info.getValue().getSocketAddress();

            InetAddress infoInetAddr = ((InetSocketAddress) infoSockAddr).getAddress();

            if (infoInetAddr.equals(addressIn)) {

                String name = info.getKey();
                getMainServer().addActiveConnection(connection, name);

                return new Packet(PacketTypes.CLIENT_HANDSHAKE, false, true, name);

            }
        }

        return null;
    }
}