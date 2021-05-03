package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class PlayerSendMessage extends PacketHandler {

    public PlayerSendMessage(Server serverIn) {
        super(serverIn, false, PacketTypes.PLAYER_SEND_MESSAGE);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        Object[] data = packetIn.getDataArray();
        String message = (String) data[data.length - 1];

        for (int i = 0; i < data.length - 1; i++) {

            String skriptPlayerName = (String) data[i];
            ProxiedPlayer bungeecordPlayer = getProxy().getPlayer(skriptPlayerName);

            if (bungeecordPlayer != null) {
                bungeecordPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
            }
        }

        return null;
    }
}
