package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;

import static tk.zulfengaming.zulfbungee.universal.util.MessageUtils.*;

public class ZulfBungeeCommand extends Command implements TabExecutor {

    private final CommandHandlerManager commandHandlerManager;

    public ZulfBungeeCommand(CommandHandlerManager handlerIn) {
        super("zulfbungee");
        this.commandHandlerManager = handlerIn;
    }

    @Override
    public void execute(CommandSender commandSender, String[] argsIn) {
        commandHandlerManager.handle(commandSender, argsIn);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return commandHandlerManager.onTabComplete(commandSender, strings);
    }
}
