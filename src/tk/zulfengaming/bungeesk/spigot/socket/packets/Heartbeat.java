package tk.zulfengaming.bungeesk.spigot.socket.packets;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.spigot.interfaces.PacketHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class Heartbeat extends PacketHandler {

    public Heartbeat(ClientConnection connection) {
        super(connection, PacketTypes.HEARTBEAT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        getConnection().getPluginInstance().log("Packet recieved !!!");
        getConnection().getPluginInstance().log(packetIn.getData()[0].toString());
        return packetIn;

    }
}
