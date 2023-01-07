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
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExprProxyServerPlayers extends SimpleExpression<ClientPlayer> {

    static {
        Skript.registerExpression(ExprProxyServerPlayers.class, ClientPlayer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] (bungeecord|bungee|proxy|velocity) players [on %-proxyservers%]");
    }

    private Expression<ClientServer> servers;

    @Override
    protected ClientPlayer[] get(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        ArrayList<ClientPlayer> playersOut = new ArrayList<>();

        Optional<Packet> request;

        if (servers != null) {

            ClientServer[] serversOut = servers.getArray(event);

            request = connection.send(new Packet(PacketTypes.PROXY_PLAYERS,
                        true, false, serversOut));


        } else {

            request = connection.send(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, new Object[0]));

        }

        if (request.isPresent()) {

            Packet packet = request.get();

            if (packet.getDataArray() != null) {

                List<ClientPlayer> playersFrom = Stream.of(packet.getDataArray())
                        .filter(Objects::nonNull)
                        .filter(ClientPlayer.class::isInstance)
                        .map(ClientPlayer.class::cast)
                        .collect(Collectors.toList());

                playersOut.addAll(playersFrom);


            }

        }

        return playersOut.toArray(new ClientPlayer[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ClientPlayer> getReturnType() {
        return ClientPlayer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "all bungeecord players";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        servers = (Expression<ClientServer>) expressions[0];
        return true;
    }
}
