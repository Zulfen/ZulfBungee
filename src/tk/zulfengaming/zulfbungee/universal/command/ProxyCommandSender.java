package tk.zulfengaming.zulfbungee.universal.command;

public interface ProxyCommandSender<P> {
   boolean isPlayer();
   boolean hasPermission(String permission);
   void sendMessage(String message);
}
