package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerPing;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventProxyMessage;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ServerMessage;

import java.net.SocketAddress;

public class ServerPingEvent extends PacketHandler {

    public ServerPingEvent(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.PROXY_PLAYER_PING);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ProxyPlayerDataContainer dataContainer = (ProxyPlayerDataContainer) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(new EventPlayerServerPing(
                (String) dataContainer.getData(), dataContainer.getPlayers()[0]));

    }
}
