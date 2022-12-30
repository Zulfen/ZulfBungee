package tk.zulfengaming.zulfbungee.spigot.elements;

import ch.njol.skript.registrations.Converters;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;

import java.util.Optional;
import java.util.UUID;

public class SkriptConverters {

    static {

        Converters.registerConverter(ProxyPlayer.class, OfflinePlayer.class, player -> Bukkit.getOfflinePlayer(player.getUuid()));

        Converters.registerConverter(OfflinePlayer.class, ProxyPlayer.class, offlinePlayer -> new ProxyPlayer(offlinePlayer.getName(), offlinePlayer.getUniqueId()));

        Converters.registerConverter(ProxyServer.class, String.class, ProxyServer::getName);

        Converters.registerConverter(ProxyPlayer.class, String.class, ProxyPlayer::getName);

        Converters.registerConverter(String.class, ProxyPlayer.class, s -> {

            Optional<Packet> playerRequest = ZulfBungeeSpigot.getPlugin().getConnection()
                    .send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, false, s));

            if (playerRequest.isPresent()) {
                Packet packet = playerRequest.get();
                if (packet.getDataArray().length != 0) {
                    UUID uuid = (UUID) packet.getDataSingle();
                    return new ProxyPlayer(s, uuid);
                }
            }

            return null;

        });

        Converters.registerConverter(String.class, ProxyServer.class, s -> {
            Optional<ProxyServer> server = ZulfBungeeSpigot.getPlugin().getConnection().getProxyServer(s);
            return server.orElse(null);
        });

    }

}
