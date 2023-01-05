package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.*;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSwitchServer<P> extends PacketHandler<P> {

    public PlayerSwitchServer(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SWITCH_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ClientPlayerDataContainer switchEvent = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientServer clientServer = (ClientServer) switchEvent.getData();

        if (clientServer != null) {

            ZulfProxyServer<P> server = getProxy().getServer(clientServer.getName());
            ZulfServerInfo<P> zulfServerInfo = server.getServerInfo();

            List<UUID> uuids = Stream.of(switchEvent.getPlayers())
                    .map(ClientPlayer::getUuid)
                    .collect(Collectors.toList());

            for (UUID uuid : uuids) {

                ZulfProxyPlayer<P> proxyPlayer = getProxy().getPlayer(uuid);

                if (zulfServerInfo != null)
                    if (proxyPlayer != null) {
                        proxyPlayer.connect(server);
                    }

            }

        }

        return null;
    }
}
