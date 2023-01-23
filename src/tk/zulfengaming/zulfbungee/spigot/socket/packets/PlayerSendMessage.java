package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.skript.util.chat.MessageComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerMessage;

import java.net.SocketAddress;
import java.util.List;

public class PlayerSendMessage extends PacketHandler {

    public PlayerSendMessage(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.PLAYER_SEND_MESSAGE);
    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        PlayerMessage playerMessage = (PlayerMessage) packetIn.getDataSingle();
        String message = playerMessage.getMessage();

        List<MessageComponent> parsed = ChatMessages.parse(message);

        for (ClientPlayer clientPlayer : playerMessage.getToPlayers()) {

            getConnection().getPluginInstance().logDebug(clientPlayer.getName());

            BaseComponent[] components = BungeeConverter.convert(parsed);

            getConnection().getPluginInstance()
                   .getServer().getPlayer(clientPlayer.getUuid()).spigot().sendMessage(components);


        }

    }
}
