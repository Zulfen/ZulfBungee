package com.zulfen.zulfbungee.core.command;

import com.zulfen.zulfbungee.core.command.util.Constants;

public interface ProxyCommandSender {
   boolean isPlayer();
   boolean hasPermission(String permission);
   void sendMessage(String message);
   default void sendPluginMessage(String pluginMessage) {
      sendMessage(Constants.MESSAGE_PREFIX + pluginMessage);
   }
}
