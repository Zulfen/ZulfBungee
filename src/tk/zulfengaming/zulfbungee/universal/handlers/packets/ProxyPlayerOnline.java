package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class ProxyPlayerOnline<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerOnline(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ClientPlayer skriptPlayer = (ClientPlayer) packetIn.getDataSingle();

        if (skriptPlayer != null) {

            Optional<ZulfProxyPlayer<P, T>> proxiedPlayer = getProxy().getPlayer(skriptPlayer);
            if (proxiedPlayer.isPresent()) {
                return new Packet(PacketTypes.PLAYER_ONLINE, false, false, true);
            }

        }

        return new Packet(PacketTypes.PLAYER_ONLINE, false, false, false);
    }
}