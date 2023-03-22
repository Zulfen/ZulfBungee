package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class PlayerSendMessage<P> extends PacketHandler<P> {

    public PlayerSendMessage(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : dataContainer.getPlayers()) {

            Optional<ZulfProxyPlayer<P>> getProxyPlayer = getProxy().getPlayer(clientPlayer);

            getProxyPlayer.ifPresent(pZulfProxyPlayer -> {
                Optional<BaseServerConnection<P>> getConnection = getMainServer().getConnection(pZulfProxyPlayer);
                getConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            });


        }


        return null;

    }
}
