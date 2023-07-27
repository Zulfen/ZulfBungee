package com.zulfen.zulfbungee.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.velocity.ZulfVelocity;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.managers.CommandHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.List;
import java.util.Optional;

public class VelocityCommand implements SimpleCommand {

    private final ZulfVelocity zulfVelocity;

    public final CommandHandlerManager<ProxyServer, Player> commandHandlerManager;

    public VelocityCommand(CommandHandlerManager<ProxyServer, Player> commandHandlerManagerIn) {
        this.commandHandlerManager = commandHandlerManagerIn;
        this.zulfVelocity = (ZulfVelocity) commandHandlerManager.getMainServer().getPluginInstance();
    }

    private ProxyCommandSender<ProxyServer, Player> getSender(Invocation invocationIn) {

        CommandSource commandSource = invocationIn.source();

        if (commandSource instanceof Player) {
            Optional<ZulfProxyPlayer<ProxyServer, Player>> apply = zulfVelocity.getPlayerConverter().apply((Player) commandSource);
            if (apply.isPresent()) {
                return apply.get();
            }

        }

        return zulfVelocity.getConsole();

    }

    @Override
    public void execute(Invocation invocation) {
        commandHandlerManager.handle(getSender(invocation), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return commandHandlerManager.onTabComplete(getSender(invocation), invocation.arguments());

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }

}
