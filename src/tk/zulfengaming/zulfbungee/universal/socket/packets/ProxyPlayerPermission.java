package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyPlayerPermission<P> extends PacketHandler<P> {

    public ProxyPlayerPermission(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientPlayer clientPlayer = dataContainer.getPlayers()[0];
        Optional<ZulfProxyPlayer<P>> getPlayer = getProxy().getPlayer(clientPlayer);

        if (getPlayer.isPresent()) {

            ZulfProxyPlayer<P> proxyPlayer = getPlayer.get();

            boolean hasPermissions = Stream.of(dataContainer.getDataArray())
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .allMatch(proxyPlayer::hasPermission);

            return new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, hasPermissions);


        }

        return new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, false);

    }

}