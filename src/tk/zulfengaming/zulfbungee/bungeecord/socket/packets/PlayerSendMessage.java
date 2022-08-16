package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;

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

            ProxiedPlayer bungeecordPlayer = getProxy().getPlayer(uuid);

            if (bungeecordPlayer != null) {
                bungeecordPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', (String) message.getData())));
            }

        }

        return null;
    }
}
