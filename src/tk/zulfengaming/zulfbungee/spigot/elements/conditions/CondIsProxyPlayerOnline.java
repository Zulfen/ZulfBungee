package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.util.Optional;

@Name("Proxy Player Online")
@Description("Checks if a proxy player is online on the network.")
public class CondIsProxyPlayerOnline extends PropertyCondition<ProxyPlayer> {

    private Expression<ProxyPlayer> player;

    static {
        register(CondIsProxyPlayerOnline.class, "online", "proxyplayers");
    }

    @Override
    public boolean check(ProxyPlayer proxyPlayer) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<Packet> response = connection.send(new Packet(PacketTypes.PLAYER_ONLINE, true, false, proxyPlayer));

        if (response.isPresent()) {
            Packet packetIn = response.get();
            return (boolean) packetIn.getDataSingle();
        }

        return false;

    }

    @Override
    protected @NotNull String getPropertyName() {
        return "online";
    }
}
