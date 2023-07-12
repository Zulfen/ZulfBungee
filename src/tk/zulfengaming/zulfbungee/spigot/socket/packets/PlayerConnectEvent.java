package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerConnect;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;

public class PlayerConnectEvent extends PacketHandler {

    public PlayerConnectEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.CONNECT_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayer player = (ClientPlayer) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerConnect(player)
        );

    }
}
