package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.bungeecord.socket.ServerConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class ClientHandshake extends PacketHandler {

    public ClientHandshake(Server serverIn) {
        super(serverIn, false, PacketTypes.CLIENT_HANDSHAKE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ServerConnection connection = getMainServer().getSocketConnection(address);
        String name = (String) packetIn.getDataSingle();

        getMainServer().addActiveConnection(connection, name);
        getMainServer().getPluginInstance().log("Server '" + name + "' added to the list of active connections!");

        return null;
    }
}