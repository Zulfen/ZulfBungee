package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

// used when you only have the player's name but not the uuid
public class ProxyPlayerUUID<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerUUID(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connectionIn) {

        String playerName = (String) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T>> proxiedPlayer = getProxy().getPlayer(playerName);

        return proxiedPlayer.map(pZulfProxyPlayer -> new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, pZulfProxyPlayer.getUuid())).orElseGet(() -> new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, new Object[0]));

    }
}