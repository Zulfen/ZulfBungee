package tk.zulfengaming.zulfbungee.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;
import tk.zulfengaming.zulfbungee.velocity.objects.VelocityPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VelocityCommand implements SimpleCommand {

    private final ZulfVelocity zulfVelocity;

    public final CommandHandlerManager<ProxyServer> commandHandlerManager;

    public VelocityCommand(CommandHandlerManager<ProxyServer> commandHandlerManagerIn) {
        this.commandHandlerManager = commandHandlerManagerIn;
        this.zulfVelocity = (ZulfVelocity) commandHandlerManager.getMainServer().getPluginInstance();
    }

    private ProxyCommandSender<ProxyServer> getSender(Invocation invocationIn) {

        CommandSource commandSource = invocationIn.source();

        if (commandSource instanceof Player) {
            return new VelocityPlayer((Player) commandSource,
                    zulfVelocity);
        } else {
            return zulfVelocity.getConsole();
        }

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
