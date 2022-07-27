package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils.*;

public class ZulfBungeeCommand extends Command implements TabExecutor {

    private final CommandHandlerManager commandHandlerManager;

    public ZulfBungeeCommand(CommandHandlerManager handlerIn) {
        super("zulfbungee");
        this.commandHandlerManager = handlerIn;
    }

    @Override
    public void execute(CommandSender commandSender, String[] argsIn) {

        if (argsIn.length > 0) {

            Optional<CommandHandler> handlerOptional = commandHandlerManager.getHandler(argsIn[0]);

            if (handlerOptional.isPresent()) {

                CommandHandler handler = handlerOptional.get();
                if (commandSender.hasPermission(handler.getPermission())) {

                    // + 1 includes main label.
                    int totalLabels = handler.getOtherLabels().length + 1;

                    String[] extraArgs = new String[0];

                    if (argsIn.length > totalLabels) {

                        int lenDifference = argsIn.length - totalLabels;
                        extraArgs = Arrays.copyOfRange(argsIn, argsIn.length - lenDifference, argsIn.length);

                    }

                    handler.handleCommand(commandSender, extraArgs);

                } else {
                    sendMessage(commandSender, "You don't have permission to run this command!");
                }

            } else {
                sendMessage(commandSender, "That sub command does not exist! Please read the documentation.");
            }

        } else {
            sendMessage(commandSender, "Please input a sub-command.");
        }



    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        ArrayList<String> newArgs = new ArrayList<>();

        String mainLabel = strings[0];

        if (mainLabel.isEmpty()) {

            for (CommandHandler handler : commandHandlerManager.getHandlers()) {
                if (commandSender.hasPermission(handler.getPermission())) {
                    newArgs.add(handler.getMainLabel());
                }
            }

        } else {

            // the - 2 accounts for the mainLabel
            int index = strings.length - 2;

            Optional<CommandHandler> commandHandlerOptional = commandHandlerManager.getHandler(mainLabel);

            if (commandHandlerOptional.isPresent()) {

                CommandHandler commandHandler = commandHandlerOptional.get();

                if (commandSender.hasPermission(commandHandler.getPermission())) {

                    int size = commandHandler.getOtherLabels().length;

                    if (index < size) {

                        newArgs.add(commandHandler.getOtherLabels()[index]);

                    } else {

                        int newIndex = index - size;
                        newArgs.addAll(commandHandler.onTab(newIndex));

                    }

                }

            }

        }

        return newArgs;

    }
}
