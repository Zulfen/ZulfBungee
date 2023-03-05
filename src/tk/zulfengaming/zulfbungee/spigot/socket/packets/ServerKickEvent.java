package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerKick;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

import java.net.SocketAddress;

public class ServerKickEvent extends PacketHandler {

    public ServerKickEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.KICK_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();

        String reason = (String) container.getData();
        ClientPlayer player = container.getPlayers()[0];

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerKick(reason, player)
        );

    }
}
