package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Optional;

public class PlayerSwitchServer<P, T> extends PacketHandler<P, T> {

    public PlayerSwitchServer(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ClientPlayerDataContainer switchEvent = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientServer clientServer = (ClientServer) switchEvent.getDataSingle();

        if (clientServer != null) {

            Optional<ZulfProxyServer<P, T>> server = getProxy().getServer(clientServer);

            if (server.isPresent()) {

                for (ClientPlayer clientPlayer : switchEvent.getPlayers()) {

                    Optional<ZulfProxyPlayer<P, T>> proxyPlayer = getProxy().getPlayer(clientPlayer);

                    proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.connect(server.get()));

                }

            }

        }

        return null;
    }
}
