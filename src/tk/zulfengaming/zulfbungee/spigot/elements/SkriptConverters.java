package tk.zulfengaming.zulfbungee.spigot.elements;

import ch.njol.skript.classes.Converter;
import ch.njol.skript.registrations.Converters;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ProxyServerInfoManager;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Optional;
import java.util.UUID;

public class SkriptConverters {

    static {

        Converters.registerConverter(ProxyPlayer.class, OfflinePlayer.class, player -> Bukkit.getOfflinePlayer(player.getUuid()));

        Converters.registerConverter(OfflinePlayer.class, ProxyPlayer.class, offlinePlayer -> new ProxyPlayer(offlinePlayer.getName(), offlinePlayer.getUniqueId()));

        Converters.registerConverter(ProxyServer.class, String.class, ProxyServer::getName);

        Converters.registerConverter(String.class, ProxyPlayer.class, s -> {

            Optional<Packet> response = ZulfBungeeSpigot.getPlugin().getConnection()
                    .send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, true, s));

            if (response.isPresent()) {

                Packet packetIn = response.get();

                if (packetIn.getDataArray().length != 0) {
                    UUID uuid = (UUID) packetIn.getDataSingle();
                    return new ProxyPlayer(s, uuid);
                }

            }

            return null;

        });

    }
}
