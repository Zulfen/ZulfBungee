package com.zulfen.zulfbungee.spigot.handlers.packets;

import org.bukkit.command.ConsoleCommandSender;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;

public class ConsoleCommand extends PacketHandler {

    public ConsoleCommand(ClientConnection<?> connectionIn) {
        super(connectionIn, false, PacketTypes.CONSOLE_EXECUTE_COMMAND);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        String command = (String) packetIn.getDataSingle();
        ZulfBungeeSpigot zulfBungeeSpigot = getConnection().getPluginInstance();

        ConsoleCommandSender consoleSender = zulfBungeeSpigot.getServer().getConsoleSender();
        zulfBungeeSpigot.getTaskManager().newMainThreadTask(() -> zulfBungeeSpigot.getServer().dispatchCommand(consoleSender, command));

    }
}
