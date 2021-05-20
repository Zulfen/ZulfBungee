package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventMessage;
import tk.zulfengaming.zulfbungee.spigot.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class ServerMessageEvent extends PacketHandler {

    public ServerMessageEvent(ClientConnection connection) {
        super(connection, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        String message = (String) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(new EventMessage(message));

        return null;

    }
}
