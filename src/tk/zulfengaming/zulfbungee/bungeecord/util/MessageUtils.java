package tk.zulfengaming.zulfbungee.bungeecord.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class MessageUtils {

    public static void sendMessage(CommandSender senderIn, String messageIn) {
        final ComponentBuilder messagePrefix = new ComponentBuilder("[")
                .color(ChatColor.WHITE)
                .bold(true)
                .append("ZulfBungee")
                .color(ChatColor.AQUA)
                .bold(true)
                .append("]")
                .color(ChatColor.WHITE)
                .bold(true)
                .append(" ")
                .bold(false);
        senderIn.sendMessage(messagePrefix.append(messageIn).color(ChatColor.WHITE).create());
    }

    public static void sendMessage(CommandSender senderIn, BaseComponent[] componentsIn) {
        final ComponentBuilder messagePrefix = new ComponentBuilder("[")
                .color(ChatColor.WHITE)
                .bold(true)
                .append("ZulfBungee")
                .color(ChatColor.AQUA)
                .bold(true)
                .append("]")
                .color(ChatColor.WHITE)
                .bold(true)
                .append(" ")
                .bold(false);
        senderIn.sendMessage(messagePrefix.append(componentsIn).create());
    }

}
