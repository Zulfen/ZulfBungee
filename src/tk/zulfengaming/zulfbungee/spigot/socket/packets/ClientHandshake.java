package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ClientUpdate;

import java.net.SocketAddress;

public class ClientHandshake extends PacketHandler {

    public ClientHandshake(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.CLIENT_UPDATE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ClientUpdate info = (ClientUpdate) packetIn.getDataSingle();

        getConnection().setClientUpdate(info);
        getConnection().requestGlobalScripts();

        return null;

    }
}
