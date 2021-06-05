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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ExprBungeePlayers extends SimpleExpression<ProxyPlayer> {

    static {
        Skript.registerExpression(ExprBungeePlayers.class, ProxyPlayer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] bungeecord players");
    }

    @Override
    protected ProxyPlayer[] get(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        try {
            Optional<Packet> request = connection.send(new Packet(PacketTypes.GLOBAL_PLAYERS,
                    true, false, null));

            if (request.isPresent()) {
                Packet packet = request.get();

                if (packet.getDataArray() != null) {

                    return Stream.of(packet.getDataArray())
                            .filter(Objects::nonNull)
                            .filter(ProxyPlayer.class::isInstance)
                            .map(ProxyPlayer.class::cast)
                            .toArray(ProxyPlayer[]::new);

                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();

            return null;
        }

        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ProxyPlayer> getReturnType() {
        return ProxyPlayer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all bungeecord players";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
