package com.zulfen.zulfbungee.universal.command.subcommands;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;

public class Debug<P, T> extends CommandHandler<P, T> {

    public Debug(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.debug", "debug");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        ZulfBungeeProxy<P, T> pluginInstance = getMainServer().getPluginInstance();
        String transportType = getMainServer().getClass().toString();
        sender.sendPluginMessage(String.format("You are running on platform: %s", pluginInstance.platformString()));
        sender.sendPluginMessage(String.format("Current plugin version: %s", pluginInstance.getVersion()));
        sender.sendPluginMessage(String.format("Current transport type: %s", transportType));
    }
}
