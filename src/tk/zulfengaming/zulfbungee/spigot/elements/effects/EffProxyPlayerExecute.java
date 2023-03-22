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
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

@Name("Proxy Player Execute Command")
@Description("Makes a proxy player execute a command (on the Spigot server)")
public class EffProxyPlayerExecute extends Effect {

    private Expression<ClientPlayer> players;
    private Expression<String> command;

    static {
        Skript.registerEffect(EffProxyPlayerExecute.class, "make [(proxy|bungeecord|bungee|velocity) player[s]] %-proxyplayers% (execute|run) [[the] command] %string%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        command = (Expression<String>) expressions[1];
        players = (Expression<ClientPlayer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        String commandExpression = command.getSingle(event);
        String commandString;

        if (commandExpression != null) {

            commandString = commandExpression;

            if (commandExpression.contains("/")) {
                commandString = commandExpression.split("/")[1];
            }

            ZulfBungeeSpigot.getPlugin().getConnectionManager().sendDirect(new Packet(PacketTypes.PLAYER_EXECUTE_COMMAND, true, true,
                    new ClientPlayerDataContainer(commandString, players.getArray(event))));

        }

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect proxy player command " + command.toString(event, b) + " for players " + players.toString(event, b);
    }
}
