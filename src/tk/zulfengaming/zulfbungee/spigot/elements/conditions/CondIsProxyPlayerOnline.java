package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.util.Optional;

public class CondIsProxyPlayerOnline extends Condition {

    private Expression<ProxyPlayer> player;

    static {
        Skript.registerCondition(CondIsProxyPlayerOnline.class, "(proxy|bungeecord|bungee) player %-proxyplayer% (1¦is|2¦is(n't| not)) online");
    }

    @Override
    public boolean check(Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        try {

            Optional<Packet> response = connection.send(new Packet(PacketTypes.PLAYER_ONLINE, true, false, player.getSingle(event)));

            if (response.isPresent()) {

                Packet packetIn = response.get();

                return (boolean) packetIn.getDataSingle();

            }

        } catch (InterruptedException ignored) {

        }

        return false;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "proxy player " + player.toString(event, b) + " status";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        player = (Expression<ProxyPlayer>) expressions[0];
        return true;
    }
}
