package com.zulfen.zulfbungee.universal.command.subcommands;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class Debug<P, T> extends CommandHandler<P, T> {

    public Debug(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.debug", "debug");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {

        ZulfBungeeProxy<P, T> pluginInstance = getMainServer().getPluginInstance();
        String transportType = getMainServer().getClass().getSimpleName();

        sender.sendPluginMessage(String.format("You are running on proxy platform: &o%s", pluginInstance.platformString()));

        if (sender instanceof ZulfProxyPlayer) {
            ZulfProxyPlayer<P, T> proxyPlayer = (ZulfProxyPlayer<P, T>) sender;
            String serverName = proxyPlayer.getServer().getName();
            Optional<ClientInfo> optionalClientInfo = getMainServer().getClientInfo(serverName);
            if (optionalClientInfo.isPresent()) {
                ClientInfo clientInfo = optionalClientInfo.get();
                String serverVersion = clientInfo.getVersionString();
                sender.sendPluginMessage(String.format("Your current server is running on platform: &o%s", serverVersion));
            }
        }

        sender.sendPluginMessage(String.format("Current plugin version: &o%s", pluginInstance.getVersion()));
        sender.sendPluginMessage(String.format("Current transport type: &o%s", transportType));
        sender.sendPluginMessage("~ Written with <3 by Zulfen ~");

    }
}
