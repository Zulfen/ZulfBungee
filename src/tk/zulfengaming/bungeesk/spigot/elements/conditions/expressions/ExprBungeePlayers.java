package tk.zulfengaming.bungeesk.spigot.elements.conditions.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.util.*;
import java.util.stream.Stream;

public class ExprBungeePlayers extends SimpleExpression<OfflinePlayer> {

    static {
        Skript.registerExpression(ExprBungeePlayers.class, OfflinePlayer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] bungeecord players");
    }

    @Override
    protected OfflinePlayer[] get(@NotNull Event event) {

        ClientConnection connection = BungeeSkSpigot.getPlugin().getConnection();

        try {
            Optional<Packet> request = connection.send(new Packet(Bukkit.getServer().getServerName(), PacketTypes.GLOBAL_PLAYERS,
                    true, false, null));

            if (request.isPresent()) {
                Packet packet = request.get();

                return Stream.of(packet.getData())
                        .filter(UUID.class::isInstance)
                        .map(UUID.class::cast)
                        .map(Bukkit::getOfflinePlayer)
                        .toArray(OfflinePlayer[]::new);

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
    public @NotNull Class<? extends OfflinePlayer> getReturnType() {
        return OfflinePlayer.class;
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
