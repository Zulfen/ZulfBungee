package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;
import java.util.stream.Stream;

public class ProxyPlayerPermission<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerPermission(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientPlayer clientPlayer = dataContainer.getPlayers()[0];
        Optional<ZulfProxyPlayer<P, T>> getPlayer = getProxy().getPlayer(clientPlayer);

        if (getPlayer.isPresent()) {

            ZulfProxyPlayer<P, T> proxyPlayer = getPlayer.get();

            boolean hasPermissions = Stream.of(dataContainer.getDataArray())
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .allMatch(proxyPlayer::hasPermission);

            return new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, hasPermissions);


        }

        return new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, false);

    }

}