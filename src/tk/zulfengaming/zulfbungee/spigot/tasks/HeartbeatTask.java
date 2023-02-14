package tk.zulfengaming.zulfbungee.spigot.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.util.Optional;

public class HeartbeatTask extends BukkitRunnable {

    private final ClientConnection connection;

    private volatile long timeBefore = 0;
    private volatile long ping = 0;

    public HeartbeatTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("HeartbeatTask");

        if (connection.isConnected().get()) {

            timeBefore = System.currentTimeMillis();
            connection.sendDirect(new Packet(PacketTypes.HEARTBEAT, true, true, ping));

        }
    }

    public long getTimeBefore() {
        return timeBefore;
    }

    public void setPing(long ping) {
        this.ping = ping;
    }

}
