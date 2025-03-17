package com.zulfen.zulfbungee.universal.command.subcommands;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;
import com.zulfen.zulfbungee.universal.managers.MainServer;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.Set;

public class Servers<P, T, C> extends CommandHandler<P, T, C> {

    public Servers(MainServer<P, T, C> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.servers", "servers");
    }

    @Override
    public void handleCommand(ProxyCommandSender sender, String[] separateArgs) {

        Set<String> serverNames = getMainServer().getActiveServerNames();

        if (!serverNames.isEmpty()) {

            sender.sendPluginMessage("Listing all connected proxy servers...");

            for (String name : serverNames) {

                Optional<ProxyServerConnection<P, T, C>> getConnection = getMainServer().getConnection(name);

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
