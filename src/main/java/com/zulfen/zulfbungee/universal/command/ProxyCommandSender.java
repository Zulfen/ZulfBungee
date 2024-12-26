package com.zulfen.zulfbungee.universal.command;

import com.zulfen.zulfbungee.universal.command.util.Constants;

public interface ProxyCommandSender<P, T, C>{
   boolean isPlayer();
   boolean hasPermission(String permission);
   void sendMessage(String message);
   default void sendPluginMessage(String pluginMessage) {
      sendMessage(Constants.MESSAGE_PREFIX + pluginMessage);
   }
}
