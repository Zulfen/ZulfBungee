package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;

public class CheckUpdate<P, T> extends CommandHandler<P, T> {

    public CheckUpdate(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        sender.sendMessage(Constants.MESSAGE_PREFIX + "Checking for an update...");
        getMainServer().getPluginInstance().getUpdater().checkUpdate(sender, true);
    }
}
