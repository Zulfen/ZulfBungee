package tk.zulfengaming.zulfbungee.universal.command;

import tk.zulfengaming.zulfbungee.universal.command.util.Constants;

public interface ProxyCommandSender<P, T>{
   boolean isPlayer();
   boolean hasPermission(String permission);
   void sendMessage(String message);
   default void sendPluginMessage(String pluginMessage) {
      sendMessage(Constants.MESSAGE_PREFIX + pluginMessage);
   }
}
