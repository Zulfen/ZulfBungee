package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSendMessage<P> extends PacketHandler<P> {

    public PlayerSendMessage(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SEND_MESSAGE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ClientPlayerDataContainer message = (ClientPlayerDataContainer) packetIn.getDataSingle();

        List<UUID> uuids = Stream.of(message.getPlayers())
                .map(ClientPlayer::getUuid)
                .collect(Collectors.toList());

        for (UUID uuid : uuids) {

            ZulfProxyPlayer<P> bungeecordPlayer = getProxy().getPlayer(uuid);

            if (bungeecordPlayer != null) {
                bungeecordPlayer.sendMessage((String) message.getData());
            }

        }

        return null;
    }
}
