package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.Set;

public class Servers<P, T> extends CommandHandler<P, T> {

    public Servers(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.servers", "servers");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {

        Set<String> serverNames = getMainServer().getServerNames();

        if (!serverNames.isEmpty()) {

            sender.sendPluginMessage("Listing all connected proxy servers...");

            for (String name : serverNames) {

                Optional<ProxyServerConnection<P, T>> getConnection = getMainServer().getConnection(name);

                if (getConnection.isPresent()) {
                    SocketAddress address = getConnection.get().getAddress();
                    sender.sendPluginMessage(String.format("%s &a(%s)", name, address));
                }

            }

        } else {

            sender.sendPluginMessage("No proxy servers are connected yet!");

        }


    }
}
