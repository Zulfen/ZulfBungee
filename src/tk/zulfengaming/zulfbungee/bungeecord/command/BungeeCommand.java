package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;

public class BungeeCommand extends Command implements TabExecutor {

    private final CommandHandlerManager<ProxyServer> commandHandlerManager;

    public BungeeCommand(CommandHandlerManager<ProxyServer> handlerIn) {
        super("zulfbungee");
        this.commandHandlerManager = handlerIn;
    }

    @Override
    public void execute(CommandSender commandSender, String[] argsIn) {

        if (commandSender instanceof ProxiedPlayer) {

            ProxiedPlayer bungeePlayer = (ProxiedPlayer) commandSender;

            commandHandlerManager.handle(new BungeePlayer(bungeePlayer), argsIn);

        } else {

            commandHandlerManager.handle(commandHandlerManager.getMainServer().getPluginInstance()
                    .getConsole(), argsIn);

        }


    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        if (commandSender instanceof ProxiedPlayer) {

            ProxiedPlayer bungeePlayer = (ProxiedPlayer) commandSender;

            return commandHandlerManager.onTabComplete(new BungeePlayer(bungeePlayer), strings);

        } else {

            return commandHandlerManager.onTabComplete(commandHandlerManager.getMainServer().getPluginInstance()
                    .getConsole(), strings);

        }

    }
}
