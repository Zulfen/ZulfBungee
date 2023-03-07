package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.Set;

public class Servers<P> extends CommandHandler<P> {

    public Servers(MainServer<P> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.servers", "servers");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {

        Set<String> serverNames = getMainServer().getServerNames();

        if (!serverNames.isEmpty()) {

            sender.sendMessage(Constants.MESSAGE_PREFIX + "Listing all connected proxy servers...");

            for (String name : serverNames) {

                Optional<BaseServerConnection<P>> getConnection = getMainServer().getConnection(name);

                if (getConnection.isPresent()) {
                    SocketAddress address = getConnection.get().getAddress();
                    sender.sendMessage(String.format("%s%s &a(%s)", Constants.MESSAGE_PREFIX, name, address));
                }

            }

        } else {

            sender.sendMessage(Constants.MESSAGE_PREFIX + "No proxy servers are connected yet!");

        }


    }
}
