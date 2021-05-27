package tk.zulfengaming.zulfbungee.spigot.task.tasks;

import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

public class HeartbeatTask implements Runnable {

    private final ClientConnection connection;

    public HeartbeatTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        if (connection.isConnected()) {

            connection.send_direct(new Packet(PacketTypes.HEARTBEAT, true, true, null));

        }
    }

}
