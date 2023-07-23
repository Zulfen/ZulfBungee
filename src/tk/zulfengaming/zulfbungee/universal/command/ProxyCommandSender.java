package tk.zulfengaming.zulfbungee.universal.command;

public interface ProxyCommandSender<P, T>{
   boolean isPlayer();
   boolean hasPermission(String permission);
   void sendMessage(String message);
}
