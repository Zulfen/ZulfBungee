package com.zulfen.zulfbungee.core.command.subcommands;

import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.core.managers.MainServer;
import com.zulfen.zulfbungee.core.ZulfProxyImpl;
import com.zulfen.zulfbungee.core.handlers.CommandHandler;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class Debug<P, T, C> extends CommandHandler<P, T, C> {

    public Debug(MainServer<P, T, C> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.debug", "debug");
    }

    @Override
    public void handleCommand(ProxyCommandSender sender, String[] separateArgs) {

        ZulfProxyImpl<P, T, C> pluginInstance = getMainServer().getImpl();
        String transportType = getMainServer().getClass().getSimpleName();

        sender.sendPluginMessage(String.format("You are running on proxy platform: &o%s", pluginInstance.platformString()));

        if (sender instanceof ZulfProxyPlayer) {
            ZulfProxyPlayer<P, T, C> proxyPlayer = (ZulfProxyPlayer<P, T, C>) sender;
            String serverName = proxyPlayer.getServer().getName();
            Optional<ClientInfo> optionalClientInfo = getMainServer().getClientInfo(serverName);
            if (optionalClientInfo.isPresent()) {
                ClientInfo clientInfo = optionalClientInfo.get();
                String serverVersion = clientInfo.versionString();
                sender.sendPluginMessage(String.format("Your current server is running on platform: &o%s", serverVersion));
            }
        }

        sender.sendPluginMessage(String.format("Current plugin version: &o%s", pluginInstance.getVersion()));
        sender.sendPluginMessage(String.format("Current transport type: &o%s", transportType));
        sender.sendPluginMessage("~ Written with <3 by Zulfen ~");

    }
}
