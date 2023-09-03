package com.zulfen.zulfbungee.universal.managers;

import com.zulfen.zulfbungee.universal.command.subcommands.Servers;
import com.zulfen.zulfbungee.universal.command.subcommands.script.ScriptList;
import com.zulfen.zulfbungee.universal.command.subcommands.script.ScriptLoad;
import com.zulfen.zulfbungee.universal.command.subcommands.script.ScriptReload;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.command.subcommands.CheckUpdate;
import com.zulfen.zulfbungee.universal.command.subcommands.Debug;
import com.zulfen.zulfbungee.universal.command.subcommands.script.ScriptUnload;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandHandlerManager<P, T> {

    private final MainServer<P, T> mainServer;

    private final ArrayList<CommandHandler<P, T>> handlers = new ArrayList<>();

    public CommandHandlerManager(MainServer<P, T> mainServerIn) {
        this.mainServer = mainServerIn;
        addHandler(new ScriptReload<>(mainServerIn));
        addHandler(new ScriptLoad<>(mainServerIn));
        addHandler(new ScriptUnload<>(mainServerIn));
        addHandler(new ScriptList<>(mainServerIn));
        addHandler(new CheckUpdate<>(mainServerIn));
        addHandler(new Debug<>(mainServerIn));
        addHandler(new Servers<>(mainServerIn));
    }

    public MainServer<P, T> getMainServer() {
        return mainServer;
    }

    public void addHandler(CommandHandler<P, T> handlerIn) {
        handlers.add(handlerIn);
    }

    public void handle(ProxyCommandSender<P, T> sender, String[] argsIn) {

        if (argsIn.length > 0) {

            Optional<CommandHandler<P, T>> handlerOptional = handlers.stream()
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

                CommandHandler<P, T> handler = handlerOptional.get();
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
                    sender.sendPluginMessage("You don't have permission to run this command!");
                }

            } else {
                sender.sendPluginMessage("That sub command does not exist! Please readSkriptQueue the documentation.");
            }

        } else {
            sender.sendPluginMessage("Please input a sub-command");
        }

    }

    public List<String> onTabComplete(ProxyCommandSender<P, T> commandSender, String[] strings) {

        if (strings.length > 0) {

            String mainLabel = strings[0];

            if (!mainLabel.isEmpty()) {

                List<String> labels = new ArrayList<>();
                for (CommandHandler<P, T> pCommandHandler : handlers) {

                    if (pCommandHandler.getMainLabel().equals(mainLabel)) {

                        String[] requiredLabels = pCommandHandler.getRequiredLabels();

                        int index = strings.length - 1;
                        int size = requiredLabels.length;

                        if (index < size) {

                            labels.add(requiredLabels[index]);

                        } else {

                            String[] afterMain = Arrays.copyOfRange(strings, 0, strings.length - 1);

                            if (Arrays.equals(requiredLabels, afterMain)) {
                                int newIndex = index - size;
                                labels.addAll(pCommandHandler.onTab(newIndex));
                            }

                        }

                    }

                }

                return labels;

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
