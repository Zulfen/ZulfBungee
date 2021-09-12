package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.UUID;

public class PlayerSendMessage extends PacketHandler {

    public PlayerSendMessage(Server serverIn) {
        super(serverIn, PacketTypes.PLAYER_SEND_MESSAGE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        Object[] data = packetIn.getDataArray();
        String message = (String) data[data.length - 1];

        for (int i = 0; i < data.length - 1; i++) {

            UUID skriptPlayerUUID = UUID.fromString((String) data[i]);
            ProxiedPlayer bungeecordPlayer = getProxy().getPlayer(skriptPlayerUUID);

            if (bungeecordPlayer != null) {
                bungeecordPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
            }
        }

        return null;
    }
}
