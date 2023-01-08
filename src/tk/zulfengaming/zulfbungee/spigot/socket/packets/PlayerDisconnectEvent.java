package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.net.SocketAddress;

public class PlayerDisconnectEvent extends PacketHandler {

    public PlayerDisconnectEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.DISCONNECT_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ClientPlayer player = (ClientPlayer) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerDisconnect(player)
        );


    }
}
