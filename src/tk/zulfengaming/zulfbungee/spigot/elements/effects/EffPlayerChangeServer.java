package tk.zulfengaming.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
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
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

@Name("Send Proxy Player to Proxy Server")
@Description("Sends a proxy player to another given proxy server.")
public class EffPlayerChangeServer extends Effect {

    private Expression<ProxyPlayer> players;
    private Expression<ProxyServer> server;

    static {
        Skript.registerEffect(EffPlayerChangeServer.class, "[(proxy|bungeecord|bungee)] (send|transfer)" +
                " %-proxyplayers% to %-proxyserver%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        players = (Expression<ProxyPlayer>) expressions[0];
        server = (Expression<ProxyServer>) expressions[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        connection.send_direct(new Packet(PacketTypes.PLAYER_SWITCH_SERVER,
                            true, true, new ProxyPlayerDataContainer(server.getSingle(event), players.getArray(event))));

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect send proxy player " + players.toString(event, b) + " to server " + server.toString(event, b);
    }
}
