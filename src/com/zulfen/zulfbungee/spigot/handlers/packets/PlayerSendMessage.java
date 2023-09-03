package com.zulfen.zulfbungee.spigot.handlers.packets;

import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.skript.util.chat.MessageComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

import java.util.List;

public class PlayerSendMessage extends PacketHandler {

    public PlayerSendMessage(ClientConnection<?> connectionIn) {
        super(connectionIn, false, PacketTypes.PLAYER_SEND_MESSAGE);
    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();
        String message = (String) dataContainer.getDataSingle();

        List<MessageComponent> parsed = ChatMessages.parse(message);

        for (ClientPlayer clientPlayer : dataContainer.getPlayers()) {

            BaseComponent[] components = BungeeConverter.convert(parsed);

            Player player = getConnection().getPluginInstance()
                    .getServer().getPlayer(clientPlayer.getUuid());

            if (player != null) {
               player.spigot().sendMessage(components);
            }


        }


    }
}
