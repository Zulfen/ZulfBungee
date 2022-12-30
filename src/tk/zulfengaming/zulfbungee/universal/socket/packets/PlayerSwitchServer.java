package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.*;
import tk.zulfengaming.zulfbungee.universal.skript.ProxyPlayerDataContainer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSwitchServer extends PacketHandler {

    public PlayerSwitchServer(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SWITCH_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        ProxyPlayerDataContainer switchEvent = (ProxyPlayerDataContainer) packetIn.getDataSingle();

        ProxyServer proxyServer = (ProxyServer) switchEvent.getData();

        if (proxyServer != null) {

            ServerInfo serverInfo = getProxy().getServersCopy().get(proxyServer.getName());

            List<UUID> uuids = Stream.of(switchEvent.getPlayers())
                    .map(ProxyPlayer::getUuid)
                    .collect(Collectors.toList());

            for (UUID uuid : uuids) {

                ProxyPlayer bungeecordPlayer = getProxy().getPlayer(uuid);

                if (serverInfo != null)
                    if (bungeecordPlayer != null) {
                        bungeecordPlayer.connect(serverInfo);
                    }

            }

        }

        return null;
    }
}
