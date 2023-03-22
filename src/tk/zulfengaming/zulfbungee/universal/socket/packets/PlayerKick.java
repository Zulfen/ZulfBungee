package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class PlayerKick<P> extends PacketHandler<P> {

    public PlayerKick(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();
        String message = (String) container.getDataSingle();

        for (ClientPlayer player : container.getPlayers()) {
            Optional<ZulfProxyPlayer<P>> proxyPlayer = getProxy().getPlayer(player);
            proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.disconnect(message));

        }

        return null;

    }
}