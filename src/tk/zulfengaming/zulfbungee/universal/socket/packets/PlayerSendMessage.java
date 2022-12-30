package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.skript.ProxyPlayerDataContainer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSendMessage extends PacketHandler {

    public PlayerSendMessage(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SEND_MESSAGE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        ProxyPlayerDataContainer message = (ProxyPlayerDataContainer) packetIn.getDataSingle();

        List<UUID> uuids = Stream.of(message.getPlayers())
                .map(ProxyPlayer::getUuid)
                .collect(Collectors.toList());

        for (UUID uuid : uuids) {

            ProxyPlayer bungeecordPlayer = getProxy().getPlayer(uuid);

            if (bungeecordPlayer != null) {
                bungeecordPlayer.sendMessage((String) message.getData());
            }

        }

        return null;
    }
}
