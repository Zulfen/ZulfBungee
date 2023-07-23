package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class PlayerSendMessage<P, T> extends PacketHandler<P, T> {

    public PlayerSendMessage(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : dataContainer.getPlayers()) {

            Optional<ZulfProxyPlayer<P, T>> getProxyPlayer = getProxy().getPlayer(clientPlayer);

            getProxyPlayer.ifPresent(pZulfProxyPlayer -> {
                Optional<ProxyServerConnection<P, T>> getConnection = getMainServer().getConnection(pZulfProxyPlayer);
                getConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            });


        }


        return null;

    }
}
