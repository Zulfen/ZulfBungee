package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class PlayerKick<P, T> extends PacketHandler<P, T> {

    public PlayerKick(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();
        String message = (String) container.getDataSingle();

        for (ClientPlayer player : container.getPlayers()) {
            Optional<ZulfProxyPlayer<P, T>> proxyPlayer = getProxy().getPlayer(player);
            proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.disconnect(message));
        }

        return null;

    }
}