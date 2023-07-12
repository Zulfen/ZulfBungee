package tk.zulfengaming.zulfbungee.spigot.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public class HeartbeatTask extends BukkitRunnable {

    private final ConnectionManager connectionManager;

    private volatile long timeBefore = 0;
    private volatile long ping = 0;

    public HeartbeatTask(ConnectionManager connectionIn) {
        this.connectionManager = connectionIn;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("HeartbeatTask");

        if (connectionManager.getRegistered() > 0) {
            timeBefore = System.currentTimeMillis();
            connectionManager.sendDirect(new Packet(PacketTypes.HEARTBEAT_PROXY, true, true, ping));
        }

    }

    public long getTimeBefore() {
        return timeBefore;
    }

    public void setPing(long ping) {
        this.ping = ping;
    }

}
