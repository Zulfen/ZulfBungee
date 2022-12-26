package tk.zulfengaming.zulfbungee.bungeecord.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils;

public class Ping extends CommandHandler {

    public Ping(MainServer mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.ping", "ping");
    }

    @Override
    public void handleCommand(CommandSender sender, String[] separateArgs) {

        String serverName = ((ProxiedPlayer) sender).getServer().getInfo().getName();
        long ping = getMainServer().getConnectionFromName(serverName).getPing();

         MessageUtils.sendMessage(sender, new ComponentBuilder("Ping: ")
                 .color(ChatColor.WHITE)
                 .append(ping + "ms")
                 .color(ChatColor.WHITE)
                 .italic(true)
                 .create());

    }
}
