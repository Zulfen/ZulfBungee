package com.zulfen.zulfbungee.spigot.elements;

import ch.njol.skript.registrations.Converters;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Optional;

public class SkriptConverters {

    static {

        Converters.registerConverter(ClientPlayer.class, OfflinePlayer.class, player -> Bukkit.getOfflinePlayer(player.getUuid()));

        Converters.registerConverter(OfflinePlayer.class, ClientPlayer.class, offlinePlayer -> new ClientPlayer(offlinePlayer.getName(), offlinePlayer.getUniqueId()));

        Converters.registerConverter(ClientServer.class, String.class, ClientServer::getName);

        Converters.registerConverter(ClientPlayer.class, String.class, ClientPlayer::getName);

        Converters.registerConverter(String.class, ClientPlayer.class, s -> {

            Optional<Packet> playerRequest = ZulfBungeeSpigot.getPlugin().getConnectionManager()
                    .send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, true, s));

            if (playerRequest.isPresent()) {
                Packet packet = playerRequest.get();
                if (packet.getDataArray().length != 0) {
                    return (ClientPlayer) packet.getDataSingle();
                }
            }

            return null;

        });

        Converters.registerConverter(String.class, ClientServer.class, s -> {
            Optional<ClientServer> server = ZulfBungeeSpigot.getPlugin().getConnectionManager().getProxyServer(s);
            return server.orElse(null);
        });

    }

}
