package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

public class PlayerKick<P> extends PacketHandler<P> {

    public PlayerKick(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer player : container.getPlayers()) {

            ZulfProxyPlayer<P> proxyPlayer = getProxy().getPlayer(player.getUuid());

            if (proxyPlayer != null) {

                String reason = "You have been kicked from the proxy.";

                if (container.getData() != null) {
                    reason = (String) container.getData();
                }

                proxyPlayer.disconnect(reason);

            }

        }

        return null;

    }
}