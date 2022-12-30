package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventProxyMessage;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

import java.net.SocketAddress;

public class ServerMessageEvent extends PacketHandler {

    public ServerMessageEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(new EventProxyMessage(message));

    }
}
