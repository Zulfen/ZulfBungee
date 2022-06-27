package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExprProxyPlayers extends SimpleExpression<ProxyPlayer> {

    static {
        Skript.registerExpression(ExprProxyPlayers.class, ProxyPlayer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] (bungeecord|bungee|proxy) players [on %-proxyservers%]");
    }

    private Expression<ProxyServer> servers;

    @Override
    protected ProxyPlayer[] get(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        ArrayList<ProxyPlayer> playersOut = new ArrayList<>();

        Optional<Packet> request;

        if (servers != null) {

            ProxyServer[] serversOut = servers.getArray(event);

            request = connection.send(new Packet(PacketTypes.PROXY_PLAYERS,
                        true, false, serversOut));


        } else {

            request = connection.send(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, new ProxyServer[0]));

        }

        if (request.isPresent()) {

            Packet packet = request.get();

            if (packet.getDataArray() != null) {

                List<ProxyPlayer> playersFrom = Stream.of(packet.getDataArray())
                        .filter(Objects::nonNull)
                        .filter(ProxyPlayer.class::isInstance)
                        .map(ProxyPlayer.class::cast)
                        .collect(Collectors.toList());

                playersOut.addAll(playersFrom);


            }

        }

        return playersOut.toArray(new ProxyPlayer[0]);
    }

    @Override
    public boolean isSingle() {
        return servers.isSingle();
    }

    @Override
    public @NotNull Class<? extends ProxyPlayer> getReturnType() {
        return ProxyPlayer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "all bungeecord players";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        servers = (Expression<ProxyServer>) expressions[0];
        return true;
    }
}
