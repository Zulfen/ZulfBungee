package tk.zulfengaming.zulfbungee.universal.command;

public interface ProxyCommandSender {
   boolean isPlayer();
   boolean hasPermission(String permission);
   void sendMessage(String message);
}
