package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
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
public class CondIsProxyPlayerOnline extends Condition {

    private Expression<ProxyPlayer> player;

    static {
        Skript.registerCondition(CondIsProxyPlayerOnline.class, "%-proxyplayer% (1¦is|2¦is(n't| not)) online");
    }

    @Override
    public boolean check(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<Packet> response = connection.send(new Packet(PacketTypes.PLAYER_ONLINE, true, false, player.getSingle(event)));

        if (response.isPresent()) {

            Packet packetIn = response.get();
            return (boolean) packetIn.getDataSingle();

        }

        return false;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "proxy player " + player.toString(event, b) + " status";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        player = (Expression<ProxyPlayer>) expressions[0];
        return true;
    }
}
