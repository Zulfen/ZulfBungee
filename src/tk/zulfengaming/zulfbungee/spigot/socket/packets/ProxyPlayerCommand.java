package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import org.bukkit.entity.Player;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerExecutableCommand;

import java.net.SocketAddress;

public class ProxyPlayerCommand extends PacketHandler {

    public ProxyPlayerCommand(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.PLAYER_EXECUTE_COMMAND);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        PlayerExecutableCommand command = (PlayerExecutableCommand) packetIn.getDataSingle();
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
