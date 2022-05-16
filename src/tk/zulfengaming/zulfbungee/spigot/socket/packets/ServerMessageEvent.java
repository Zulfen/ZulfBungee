package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventMessage;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ServerMessage;

import java.net.SocketAddress;

public class ServerMessageEvent extends PacketHandler {

    public ServerMessageEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(new EventMessage(message));

    }
}
