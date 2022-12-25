package tk.zulfengaming.zulfbungee.spigot.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

public class HeartbeatTask extends BukkitRunnable {

    private final ClientConnection connection;

    public HeartbeatTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("HeartbeatTask");
        if (connection.isConnected().get()) {
            connection.sendDirect(new Packet(PacketTypes.HEARTBEAT, true, true, null));
        }
    }

}
