package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSwitchServer extends PacketHandler {

    public PlayerSwitchServer(Server serverIn) {
        super(serverIn, PacketTypes.PLAYER_SWITCH_SERVER);

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

                ProxiedPlayer bungeecordPlayer = getProxy().getPlayer(uuid);

                if (serverInfo != null)
                    if (bungeecordPlayer != null) {
                        bungeecordPlayer.connect(serverInfo);
                    }

            }

        }

        return null;
    }
}
