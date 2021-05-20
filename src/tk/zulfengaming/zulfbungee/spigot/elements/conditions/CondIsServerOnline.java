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
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyServer;

import java.util.Optional;

public class CondIsServerOnline extends Condition {

    Expression<ProxyServer> server;

    static {
        Skript.registerCondition(CondIsServerOnline.class, "proxy server %-proxyserver% (1¦is|2¦is(n't| not)) online");
    }

    @Override
    public boolean check(Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        try {

            Optional<Packet> response = connection.send(new Packet(PacketTypes.SERVER_ONLINE, true, false, server.getSingle(event).getName()));

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
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        server = (Expression<ProxyServer>) expressions[0];
        return true;
    }
}
