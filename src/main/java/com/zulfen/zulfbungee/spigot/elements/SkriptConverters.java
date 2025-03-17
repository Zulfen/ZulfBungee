package com.zulfen.zulfbungee.spigot.elements;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import org.bukkit.entity.Player;
import org.skriptlang.skript.lang.converter.Converters;

public class SkriptConverters {

    static {

        Converters.registerConverter(ClientPlayer.class, OfflinePlayer.class, player -> Bukkit.getOfflinePlayer(player.getUuid()));

        Converters.registerConverter(OfflinePlayer.class, ClientPlayer.class, offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Player onlinePlayer = offlinePlayer.getPlayer();
                if (onlinePlayer != null) {
                    return new ClientPlayer(onlinePlayer.getName(), onlinePlayer.getUniqueId(), onlinePlayer.getAddress());
                }
                return null;
            } else {
                return new ClientPlayer(offlinePlayer.getName(), offlinePlayer.getUniqueId());
            }
        });

        Converters.registerConverter(ClientServer.class, String.class, ClientServer::name);

        Converters.registerConverter(ClientPlayer.class, String.class, ClientPlayer::getName);

    }

}
