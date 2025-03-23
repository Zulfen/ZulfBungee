package com.zulfen.zulfbungee.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.core.managers.CommandHandlerManager;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;

import java.util.List;
import java.util.Optional;

import org.spongepowered.configurate.ConfigurationNode;

public class VelocityCommand implements SimpleCommand {

    private final ZulfVelocityImpl zulfVelocityPlugin;

    public final CommandHandlerManager<ProxyServer, Player, ConfigurationNode> commandHandlerManager;

    public VelocityCommand(CommandHandlerManager<ProxyServer, Player, ConfigurationNode> commandHandlerManagerIn) {
        this.commandHandlerManager = commandHandlerManagerIn;
        this.zulfVelocityPlugin = (ZulfVelocityImpl) commandHandlerManager.getMainServer().getImpl();
    }

    private ProxyCommandSender getSender(Invocation invocationIn) {

        CommandSource commandSource = invocationIn.source();

        if (commandSource instanceof Player) {
            Optional<ZulfProxyPlayer<ProxyServer, Player, ConfigurationNode>> apply = zulfVelocityPlugin.getPlayerConverter().apply((Player) commandSource);
            if (apply.isPresent()) {
                return apply.get();
            }

        }

        return zulfVelocityPlugin.getConsole();

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
