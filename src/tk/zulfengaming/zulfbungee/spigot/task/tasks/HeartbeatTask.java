package tk.zulfengaming.zulfbungee.spigot.task.tasks;

import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.util.Optional;

public class HeartbeatTask implements Runnable {

    private final ClientConnection connection;

    public HeartbeatTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        if (connection.isConnected()) {

            double simpleKey = Math.random();

            try {
                Optional<Packet> send = connection.send(new Packet(
                        PacketTypes.HEARTBEAT, true, true, new Object[]{simpleKey}));



            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
