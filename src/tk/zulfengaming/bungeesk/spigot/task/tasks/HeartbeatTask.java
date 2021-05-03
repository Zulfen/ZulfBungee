package tk.zulfengaming.bungeesk.spigot.task.tasks;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

public class HeartbeatTask implements Runnable {

    private final ClientConnection connection;

    public HeartbeatTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        if (connection.isConnected()) {

            double simpleKey = Math.random();

            connection.send_direct(new Packet(
                    PacketTypes.HEARTBEAT, true, true, new Object[]{simpleKey}));

        }
    }

}
