package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventProxyMessage;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

public class ServerMessageEvent extends PacketHandler {

    public ServerMessageEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(new EventProxyMessage(message));

    }
}
