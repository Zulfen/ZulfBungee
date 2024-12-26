package com.zulfen.zulfbungee.bungeecord.command;

import com.zulfen.zulfbungee.universal.managers.CommandHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.util.Collections;
import java.util.Optional;

public class BungeeCommand extends Command implements TabExecutor {

    private final CommandHandlerManager<ProxyServer, ProxiedPlayer, Configuration> commandHandlerManager;

    public BungeeCommand(CommandHandlerManager<ProxyServer, ProxiedPlayer, Configuration> handlerIn) {
        super("zulfbungee");
        this.commandHandlerManager = handlerIn;
    }

    @Override
    public void execute(CommandSender commandSender, String[] argsIn) {

        if (commandSender instanceof ProxiedPlayer bungeePlayer) {

            Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer, Configuration>> proxyPlayer = commandHandlerManager.getMainServer()
                    .getImpl().getPlayerConverter().apply(bungeePlayer);
            proxyPlayer.ifPresent(player -> commandHandlerManager.handle(player, argsIn));

        } else {

            commandHandlerManager.handle(commandHandlerManager.getMainServer().getImpl()
                    .getConsole(), argsIn);

        }


    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        if (commandSender instanceof ProxiedPlayer bungeePlayer) {

            Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer, Configuration>> proxyPlayer = commandHandlerManager.getMainServer()
                    .getImpl().getPlayerConverter().apply(bungeePlayer);

            if (proxyPlayer.isPresent()) {
                return commandHandlerManager.onTabComplete(proxyPlayer.get(), strings);
            }

            return Collections.emptyList();

        } else {

            return commandHandlerManager.onTabComplete(commandHandlerManager.getMainServer().getImpl()
                    .getConsole(), strings);

        }

    }
}
