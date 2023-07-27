package com.zulfen.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ConsoleExecutableCommand;

@Name("Proxy Server Execute Command")
@Description("Makes a proxy server execute a command. Keep in mind, it's been executed from the console and has full permissions!")
public class EffProxyServerExecute extends Effect {

    private Expression<ClientServer> servers;
    private Expression<String> command;

    static {
        Skript.registerEffect(EffProxyServerExecute.class, "make [[(proxy|bungeecord|bungee|velocity)] server[s]] %-proxyservers% (execute|run) [[the] command] %string%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        command = (Expression<String>) expressions[1];
        servers = (Expression<ClientServer>) expressions[0];
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

            ZulfBungeeSpigot.getPlugin().getConnectionManager().sendDirect(new Packet(PacketTypes.CONSOLE_EXECUTE_COMMAND, true, true,
                    new ConsoleExecutableCommand(servers.getArray(event), commandString)));

        }

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect console command " + command.toString(event, b) + " for servers " + servers.toString(event, b);
    }
}
