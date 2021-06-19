package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerKick;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyKick;

import java.net.SocketAddress;

public class ServerKickEvent extends PacketHandler {

    public ServerKickEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.KICK_EVENT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyKick kick = (ProxyKick) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerKick(kick.getReason(), kick.getPlayer())
        );

        return null;

    }
}
