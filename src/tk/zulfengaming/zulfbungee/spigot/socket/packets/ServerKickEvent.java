package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerKick;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;

import java.net.SocketAddress;

public class ServerKickEvent extends PacketHandler {

    public ServerKickEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.KICK_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ProxyPlayerDataContainer container = (ProxyPlayerDataContainer) packetIn.getDataSingle();

        String reason = (String) container.getData();
        ProxyPlayer player = container.getPlayers()[0];

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerKick(reason, player)
        );

    }
}
