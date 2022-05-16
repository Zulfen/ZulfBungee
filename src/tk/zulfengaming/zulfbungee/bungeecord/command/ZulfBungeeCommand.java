package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;

import java.util.Arrays;
import java.util.Optional;

public class ZulfBungeeCommand extends Command {

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

                if (argsIn.length > handler.getLabels().length) {

                    int lenDifference = argsIn.length - handler.getLabels().length;

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
}
