package com.zulfen.zulfbungee.spigot.handlers.packets;

import org.bukkit.entity.Player;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

public class ProxyPlayerCommand extends PacketHandler {

    public ProxyPlayerCommand(Connection<?> connectionIn) {
        super(connectionIn, true, PacketTypes.PLAYER_EXECUTE_COMMAND);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayerDataContainer playerDataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ZulfBungeeSpigot zulfBungeeSpigot = getConnection().getPluginInstance();

        for (ClientPlayer clientPlayer : playerDataContainer.getPlayers()) {

            Player bukkitPlayer = zulfBungeeSpigot.getServer().getPlayer(clientPlayer.getUuid());

            if (bukkitPlayer != null) {
                zulfBungeeSpigot.getTaskManager().newMainThreadTask(() -> {
                    bukkitPlayer.performCommand((String) playerDataContainer.getDataSingle());
                    return null;
                });
            }

        }

    }
}
