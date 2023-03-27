package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.*;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Optional;

public class PlayerSwitchServer<P> extends PacketHandler<P> {

    public PlayerSwitchServer(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ClientPlayerDataContainer switchEvent = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientServer clientServer = (ClientServer) switchEvent.getDataSingle();

        if (clientServer != null) {

            Optional<ZulfProxyServer> server = getProxy().getServer(clientServer);

            if (server.isPresent()) {

                for (ClientPlayer clientPlayer : switchEvent.getPlayers()) {

                    Optional<ZulfProxyPlayer<P>> proxyPlayer = getProxy().getPlayer(clientPlayer);

                    proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.connect(server.get()));

                }

            }

        }

        return null;
    }
}
