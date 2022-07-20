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

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ExprProxyPlayer extends SimpleExpression<ProxyPlayer> {

    private Expression<String> playerName;

    static {
        Skript.registerExpression(ExprProxyPlayer.class, ProxyPlayer.class, ExpressionType.SIMPLE, "(proxy|bungeecord|bungee) player [(named|called)] %string%");
    }

    @Override
    protected ProxyPlayer[] get(@NotNull Event event) {

        String nameString = playerName.getSingle(event);
        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<Packet> response = connection.send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, true, nameString));

        if (response.isPresent()) {

            Packet packetIn = response.get();

            if (packetIn.getDataArray().length != 0) {
                UUID uuid = (UUID) packetIn.getDataSingle();
                return new ProxyPlayer[]{new ProxyPlayer(nameString, uuid)};
            }
        }

        return null;

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ProxyPlayer> getReturnType() {
        return ProxyPlayer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "proxy server(s): " + Arrays.toString(playerName.getArray(event));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        playerName = (Expression<String>) expressions[0];
        return true;
    }
}
