package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;

import java.util.*;

public class ZulfBungeeCommand extends Command implements TabExecutor {

    private final CommandHandlerManager commandHandlerManager;

    // TODO: make this a TextComponent, so we don't have to bring out the legacy text stuff.
    public static final String COMMAND_PREFIX = "&f&l[&b&lZulfBungee&f&l]&r ";

    public ZulfBungeeCommand(CommandHandlerManager handlerIn) {
        super("zulfbungee");
        this.commandHandlerManager = handlerIn;
    }

    @Override
    public void execute(CommandSender commandSender, String[] argsIn) {

        Optional<CommandHandler> handlerOptional = commandHandlerManager.getHandler(argsIn);

        if (handlerOptional.isPresent()) {

            CommandHandler handler = handlerOptional.get();
            if (commandSender.hasPermission(handler.getPermission())) {

                String[] extraArgs = new String[0];

                if (argsIn.length > handler.getRequiredLabels().length) {

                    int lenDifference = argsIn.length - handler.getRequiredLabels().length;
                    extraArgs = Arrays.copyOfRange(argsIn, argsIn.length - lenDifference, argsIn.length);

                }

                handler.handleCommand(commandSender, extraArgs);

            } else {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', COMMAND_PREFIX + "You don't have permission to run this command!")));
            }

        } else {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                    ('&', COMMAND_PREFIX + "That sub command does not exist! Please read the documentation.")));
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        ArrayList<String> newArgs = new ArrayList<>();
        int index = strings.length - 1;

        for (CommandHandler commandHandler : commandHandlerManager.getHandlers()) {

            if (commandSender.hasPermission(commandHandler.getPermission())) {

                int size = commandHandler.getRequiredLabels().length;

                if (index < size) {

                    newArgs.add(commandHandler.getRequiredLabels()[index]);

                } else {

                    int newIndex = index - size;
                    newArgs.addAll(commandHandler.onTab(newIndex));

                }

            }

        }

        return newArgs;

    }
}
