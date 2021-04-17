package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import net.md_5.bungee.api.chat.TextComponent;
import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.UUID;

public class PlayerSendMessage extends PacketHandler {

    public PlayerSendMessage(Server serverIn) {
        super(serverIn, false, PacketTypes.PLAYER_SEND_MESSAGE);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        Object[] data = packetIn.getData();

        for (int i = 0; i < data.length - 1; i++) {

            UUID uuid = UUID.fromString((String) data[i]);

            getProxy().getPlayer(uuid).sendMessage(TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                    (String) data[data.length - 1])));
        }

        return null;
    }
}
