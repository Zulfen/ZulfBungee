package tk.zulfengaming.zulfbungee.bungeecord.handlers;

import tk.zulfengaming.zulfbungee.bungeecord.command.subcommands.ScriptReload;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


public class CommandHandlerManager {

    private final Server server;
    private final ArrayList<CommandHandler> handlers = new ArrayList<>();

    public CommandHandlerManager(Server serverIn) {
        this.server = serverIn;
        handlers.add(new ScriptReload(serverIn));
    }

    public Server getMainServer() {
        return server;
    }

    public ArrayList<CommandHandler> getHandlers() {
        return handlers;
    }

    public Optional<CommandHandler> getHandler(String[] argsIn) {

        // args from base command I call labels instead
        for (CommandHandler handler : handlers) {

            String[] requiredLabels = handler.getRequiredLabels();
            String[] argCheck = argsIn;

            if (argsIn.length != requiredLabels.length) {
                argCheck = Arrays.copyOfRange(argsIn, 0, requiredLabels.length);
            }

            int counter = 0;

            for (int i = 0; i < requiredLabels.length; i++) {

                String requiredLabel = requiredLabels[i];
                String argIn = argsIn[i];

                if (requiredLabel.equalsIgnoreCase(argIn)) {
                    counter += 1;
                }

            }

            if (requiredLabels.length == counter) {
                return Optional.of(handler);
            }

        }

        return Optional.empty();

    }
}
