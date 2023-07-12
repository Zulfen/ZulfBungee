package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeeServer;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Collections;
import java.util.Optional;

public class BungeeCommand extends Command implements TabExecutor {

    private final CommandHandlerManager<ProxyServer, ProxiedPlayer> commandHandlerManager;

    public BungeeCommand(CommandHandlerManager<ProxyServer, ProxiedPlayer> handlerIn) {
        super("zulfbungee");
        this.commandHandlerManager = handlerIn;
    }

    @Override
    public void execute(CommandSender commandSender, String[] argsIn) {

        if (commandSender instanceof ProxiedPlayer) {

            ProxiedPlayer bungeePlayer = (ProxiedPlayer) commandSender;

            commandHandlerManager.handle(new BungeePlayer(bungeePlayer, new BungeeServer(bungeePlayer.getServer().getInfo())), argsIn);

        } else {

            commandHandlerManager.handle(commandHandlerManager.getMainServer().getPluginInstance()
                    .getConsole(), argsIn);

        }


    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        if (commandSender instanceof ProxiedPlayer) {

            ProxiedPlayer bungeePlayer = (ProxiedPlayer) commandSender;
            Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> proxyPlayer = commandHandlerManager.getMainServer()
                    .getPluginInstance().getPlayerConverter().apply(bungeePlayer);

            if (proxyPlayer.isPresent()) {
                return commandHandlerManager.onTabComplete(proxyPlayer.get(), strings);
            }

            return Collections.emptyList();

        } else {

            return commandHandlerManager.onTabComplete(commandHandlerManager.getMainServer().getPluginInstance()
                    .getConsole(), strings);

        }

    }
}
