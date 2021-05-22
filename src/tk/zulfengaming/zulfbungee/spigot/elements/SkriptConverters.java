package tk.zulfengaming.zulfbungee.spigot.elements;

import ch.njol.skript.registrations.Converters;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyServer;

public class SkriptConverters {

    static {

        Converters.registerConverter(ProxyPlayer.class, OfflinePlayer.class, player -> Bukkit.getOfflinePlayer(player.getUuid()));

        Converters.registerConverter(OfflinePlayer.class, ProxyPlayer.class, offlinePlayer -> new ProxyPlayer(offlinePlayer.getName(), offlinePlayer.getUniqueId()));

        Converters.registerConverter(ProxyServer.class, String.class, ProxyServer::getName);
    }
}
