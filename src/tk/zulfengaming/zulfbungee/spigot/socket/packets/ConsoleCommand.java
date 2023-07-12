package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import org.bukkit.command.ConsoleCommandSender;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public class ConsoleCommand extends PacketHandler {

    public ConsoleCommand(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.CONSOLE_EXECUTE_COMMAND);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        String command = (String) packetIn.getDataSingle();
        ZulfBungeeSpigot zulfBungeeSpigot = getConnection().getPluginInstance();

        ConsoleCommandSender consoleSender = zulfBungeeSpigot.getServer().getConsoleSender();

        zulfBungeeSpigot.getTaskManager().newMainThreadTask(() -> {
            zulfBungeeSpigot.getServer().dispatchCommand(consoleSender, command);
            return null;
        });

    }
}
