package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import ch.njol.skript.Skript;
import org.bukkit.entity.Player;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerCommand;

import java.net.SocketAddress;
import java.util.concurrent.Callable;

public class ProxyPlayerCommand extends PacketHandler {

    public ProxyPlayerCommand(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.EXECUTE_COMMAND);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        PlayerCommand command = (PlayerCommand) packetIn.getDataSingle();
        ZulfBungeeSpigot zulfBungeeSpigot = getConnection().getPluginInstance();

        for (ClientPlayer clientPlayer : command.getPlayers()) {

            Player bukkitPlayer = zulfBungeeSpigot.getServer().getPlayer(clientPlayer.getUuid());

            if (bukkitPlayer != null) {
                zulfBungeeSpigot.getTaskManager().newMainThreadTask(() -> {
                    bukkitPlayer.performCommand(command.getCommand());
                    return null;
                });
            }

        }

    }
}
