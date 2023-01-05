package tk.zulfengaming.zulfbungee.universal.managers;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.CheckUpdate;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.Debug;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.Ping;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.script.ScriptLoad;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.script.ScriptReload;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.script.ScriptUnload;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandlerManager<P> {

    private final MainServer<P> mainServer;

    private final ArrayList<CommandHandler<P>> handlers = new ArrayList<>();

    public CommandHandlerManager(MainServer<P> mainServerIn) {
        this.mainServer = mainServerIn;
        addHandler(new ScriptReload<>(mainServerIn));
        addHandler(new ScriptLoad<>(mainServerIn));
        addHandler(new ScriptUnload<>(mainServerIn));
        addHandler(new CheckUpdate<>(mainServerIn));
        addHandler(new Ping<>(mainServerIn));
        addHandler(new Debug<>(mainServerIn));
    }

    public MainServer<P> getMainServer() {
        return mainServer;
    }

    public void addHandler(CommandHandler<P> handlerIn) {
        handlers.add(handlerIn);
    }

    public void handle(ProxyCommandSender<P> sender, String[] argsIn) {

        if (argsIn.length > 0) {

            Optional<CommandHandler<P>> handlerOptional = handlers.stream()
                    .filter(pCommandHandler -> {

                        String[] requiredLabels = pCommandHandler.getRequiredLabels();

                        int counter = 0;

                        for (int i = 0; i < argsIn.length; i++) {

                            if (i < requiredLabels.length) {

                                String requiredLabel = requiredLabels[i];

                                String argIn = argsIn[i];

                                if (requiredLabel.equalsIgnoreCase(argIn)) {
                                    counter += 1;
                                }

                            }

                        }

                        return requiredLabels.length == counter;

                    })
                    .findFirst();

            if (handlerOptional.isPresent()) {

                CommandHandler<P> handler = handlerOptional.get();
                String mainPermission = handler.getBasePermission();

                if (sender.hasPermission(mainPermission)) {

                    int totalLabels = handler.getRequiredLabels().length;

                    String[] extraArgs = new String[0];
                    if (argsIn.length > totalLabels) {
                        int lenDifference = argsIn.length - totalLabels;
                        extraArgs = Arrays.copyOfRange(argsIn, argsIn.length - lenDifference, argsIn.length);
                    }

                    handler.handleCommand(sender, extraArgs);

                } else {
                    sender.sendMessage(Constants.MESSAGE_PREFIX + "You don't have permission to run this command!");
                }

            } else {
                sender.sendMessage(Constants.MESSAGE_PREFIX + "That sub command does not exist! Please read the documentation.");
            }

        } else {
            sender.sendMessage(Constants.MESSAGE_PREFIX + "Please input a sub-command");
        }

    }

    public List<String> onTabComplete(ProxyCommandSender<P> commandSender, String[] strings) {

        if (strings.length > 0) {

            String mainLabel = strings[0];

            if (!mainLabel.isEmpty()) {

                return handlers.stream()
                        .filter(pCommandHandler -> pCommandHandler.getMainLabel().equals(mainLabel))
                        .filter(pCommandHandler -> commandSender.hasPermission(pCommandHandler.getBasePermission()))
                        .filter(pCommandHandler -> strings.length <= pCommandHandler.getRequiredLabels().length)
                        .map(pCommandHandler -> pCommandHandler.getRequiredLabels()[Math.min(pCommandHandler.getRequiredLabels().length - 1, Math.max(0, strings.length - 1))])
                        .collect(Collectors.toList());

            } else {

                return handlers.stream()
                        .filter(pCommandHandler -> commandSender.hasPermission(pCommandHandler.getBasePermission()))
                        .map(CommandHandler::getMainLabel)
                        .collect(Collectors.toList());

            }


        } else {

            return handlers.stream()
                    .filter(pCommandHandler -> commandSender.hasPermission(pCommandHandler.getBasePermission()))
                    .map(CommandHandler::getMainLabel)
                    .collect(Collectors.toList());

        }


    }


}
