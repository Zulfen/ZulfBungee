package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.net.SocketAddress;

public class ServerSwitchEvent extends PacketHandler {

    public ServerSwitchEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.SERVER_SWITCH_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ClientPlayer player = (ClientPlayer) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerSwitchServer(player)
        );

    }
}
