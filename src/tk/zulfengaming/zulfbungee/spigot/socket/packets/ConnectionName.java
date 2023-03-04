package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.net.SocketAddress;

public class ConnectionName extends PacketHandler {

    public ConnectionName(Connection connectionIn) {
        super(connectionIn, PacketTypes.CONNECTION_NAME);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        String name = (String) packetIn.getDataSingle();
        getConnection().getConnectionManager().addNamedConnection(name, getConnection());

    }
}
