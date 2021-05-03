package tk.zulfengaming.bungeesk.spigot.elements;

import ch.njol.skript.classes.Converter;
import ch.njol.skript.registrations.Converters;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import tk.zulfengaming.bungeesk.universal.utilclasses.skript.ProxyPlayer;

public class SkriptConverters {

    static {

        Converters.registerConverter(ProxyPlayer.class, OfflinePlayer.class, new Converter<ProxyPlayer, OfflinePlayer>() {
            @Override
            public OfflinePlayer convert(ProxyPlayer player) {
                return Bukkit.getOfflinePlayer(player.getUuid());
            }
        });

        Converters.registerConverter(OfflinePlayer.class, ProxyPlayer.class, new Converter<OfflinePlayer, ProxyPlayer>() {
            @Override
            public ProxyPlayer convert(OfflinePlayer offlinePlayer) {
                return new ProxyPlayer(offlinePlayer.getName(), offlinePlayer.getUniqueId());
            }
        });
    }
}
