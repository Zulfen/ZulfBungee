package tk.zulfengaming.zulfbungee.universal.managers;

import tk.zulfengaming.zulfbungee.universal.command.subcommands.CheckUpdate;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.Ping;
import tk.zulfengaming.zulfbungee.universal.command.subcommands.script.ScriptReload;
import tk.zulfengaming.zulfbungee.universal.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.Constants;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;

import java.util.*;

public class CommandHandlerManager<P> {

    private final MainServer<P> mainServer;

    private final HashMap<String, CommandHandler<P>> handlers = new HashMap<>();

    public CommandHandlerManager(MainServer<P> mainServerIn) {
        this.mainServer = mainServerIn;
        addHandler(new ScriptReload<>(mainServerIn));
        addHandler(new CheckUpdate<>(mainServerIn));
        addHandler(new Ping<>(mainServerIn));
    }

    public MainServer<P> getMainServer() {
        return mainServer;
    }

    public void addHandler(CommandHandler<P> handlerIn) {
        handlers.put(handlerIn.getMainLabel(), handlerIn);
    }

    public void handle(ProxyCommandSender<P> sender, String[] argsIn) {

        if (argsIn.length > 0) {

            Optional<CommandHandler<P>> handlerOptional = matchToHandler(argsIn);

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

    public Iterable<String> onTabComplete(ProxyCommandSender<P> commandSender, String[] strings) {

        ArrayList<String> newArgs = new ArrayList<>();
        String mainLabel = strings[0];

        if (mainLabel.isEmpty()) {

            for (CommandHandler<P> handler : handlers.values()) {
                if (commandSender.hasPermission(handler.getBasePermission())) {
                    newArgs.add(handler.getMainLabel());
                }
            }

        } else {

            int index = strings.length - 1;

            Optional<CommandHandler<P>> commandHandlerOptional = mainLabelToHandler(mainLabel);

            if (commandHandlerOptional.isPresent()) {

                CommandHandler<P> commandHandler = commandHandlerOptional.get();
                if (commandSender.hasPermission(commandHandler.getBasePermission())) {

                    int size = commandHandler.getRequiredLabels().length;

                    if (index < size) {

                        newArgs.add(commandHandler.getRequiredLabels()[index]);

                    } else {

                        int newIndex = index - size;
                        newArgs.addAll(commandHandler.onTab(newIndex));

                    }

                }

            }

        }

        return newArgs;

    }

    private Optional<CommandHandler<P>> mainLabelToHandler(String mainLabelIn){
        return Optional.ofNullable(handlers.get(mainLabelIn));
    }

    private Optional<CommandHandler<P>> matchToHandler(String[] argsIn) {

        Optional<CommandHandler<P>> handlerOptional = mainLabelToHandler(argsIn[0]);

        if (handlerOptional.isPresent()) {

            CommandHandler<P> handler = handlerOptional.get();
            String[] requiredLabels = handler.getRequiredLabels();

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

            if (requiredLabels.length == counter) {
                return Optional.of(handler);
            }

        }

        return Optional.empty();

    }
}
