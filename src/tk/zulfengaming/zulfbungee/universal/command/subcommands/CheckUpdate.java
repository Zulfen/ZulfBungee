package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;

public class CheckUpdate<P> extends CommandHandler<P> {

    public CheckUpdate(MainServer<P> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {
        sender.sendMessage("Checking for an update...");
        getMainServer().getPluginInstance().getUpdater().checkUpdate(sender, true);
    }
}
