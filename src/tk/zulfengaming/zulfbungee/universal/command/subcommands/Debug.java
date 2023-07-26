package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;

public class Debug<P, T> extends CommandHandler<P, T> {

    public Debug(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.debug", "debug");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        ZulfBungeeProxy<P, T> pluginInstance = getMainServer().getPluginInstance();
        String transportType = pluginInstance.getConfig().getString("transport-type");
        sender.sendPluginMessage(String.format("You are running on platform: %s", pluginInstance.platformString()));
        sender.sendPluginMessage(String.format("Current plugin version: %s", pluginInstance.getVersion()));
        sender.sendPluginMessage(String.format("Current transport type: %s", transportType));
    }
}
