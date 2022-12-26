package tk.zulfengaming.zulfbungee.spigot.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.util.Optional;

public class HeartbeatTask extends BukkitRunnable {

    private final ClientConnection connection;

    private volatile long ping = 0;

    public HeartbeatTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("HeartbeatTask");

        if (connection.isConnected().get()) {

            long timeBefore = System.currentTimeMillis();
            Optional<Packet> heartbeatSent = connection.send(new Packet(PacketTypes.HEARTBEAT, true, false, ping));

            if (heartbeatSent.isPresent()) {
                ping = System.currentTimeMillis() - timeBefore;
            }

        }
    }

}
