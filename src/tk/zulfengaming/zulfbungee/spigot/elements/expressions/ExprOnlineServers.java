package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ExprOnlineServers extends SimpleExpression<ProxyServer> {

    static {
        Skript.registerExpression(ExprOnlineServers.class, ProxyServer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] online servers");
    }

    @Override
    protected ProxyServer[] get(Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        try {
            Optional<Packet> request = connection.send(new Packet(PacketTypes.GLOBAL_SERVERS,
                    true, false, null));

            if (request.isPresent()) {
                Packet packet = request.get();

                return Stream.of(packet.getDataArray())
                        .filter(Objects::nonNull)
                        .filter(ProxyServer.class::isInstance)
                        .map(ProxyServer.class::cast)
                        .toArray(ProxyServer[]::new);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ProxyServer> getReturnType() {
        return ProxyServer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
