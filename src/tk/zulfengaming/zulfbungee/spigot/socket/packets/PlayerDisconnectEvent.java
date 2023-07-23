package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

public class PlayerDisconnectEvent extends PacketHandler {

    public PlayerDisconnectEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.DISCONNECT_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerDisconnect(dataContainer.getPlayers()[0], (ClientServer) dataContainer.getDataSingle())
        );


    }
}
