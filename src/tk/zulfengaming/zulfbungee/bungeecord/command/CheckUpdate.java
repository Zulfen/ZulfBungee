package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

public class CheckUpdate extends CommandHandler {

    public CheckUpdate(Server serverIn) {
        super(serverIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(CommandSender sender, String[] separateArgs) {
        getMainServer().getPluginInstance().checkUpdate(sender, true);
    }
}
