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
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ExprProxyPlayer extends SimpleExpression<ProxyPlayer> {

    private Expression<String> playerNames;

    static {
        Skript.registerExpression(ExprProxyPlayer.class, ProxyPlayer.class, ExpressionType.SIMPLE, "(proxy|bungeecord|bungee) player[s] [(named|called)] %strings%");
    }

    @Override
    protected ProxyPlayer @NotNull [] get(@NotNull Event event) {

        ArrayList<ProxyPlayer> proxyPlayers = new ArrayList<>();

        for (String playerName : playerNames.getArray(event)) {

            Optional<Packet> response = ZulfBungeeSpigot.getPlugin().getConnection()
                    .send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, true, playerName));

            if (response.isPresent()) {

                Packet packetIn = response.get();

                if (packetIn.getDataArray().length != 0) {
                    UUID uuid = (UUID) packetIn.getDataSingle();
                    proxyPlayers.add(new ProxyPlayer(playerName, uuid));
                }

            }

        }

        return proxyPlayers.toArray(new ProxyPlayer[0]);

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
    public @NotNull String toString(Event event, boolean b) {
        return "proxy server(s): " + Arrays.toString(playerNames.getArray(event));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        playerNames = (Expression<String>) expressions[0];
        return true;
    }
}
