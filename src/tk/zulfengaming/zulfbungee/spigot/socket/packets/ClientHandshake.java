package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class ClientHandshake extends PacketHandler {

    public ClientHandshake(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.CLIENT_HANDSHAKE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        getConnection().setServerName((String) packetIn.getDataSingle());

        return null;

    }
}
