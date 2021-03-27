package tk.zulfengaming.bungeesk.spigot.task;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientManager;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Future;

public class HeartbeatTask extends ClientListener implements Runnable {

    private final ClientManager clientManager;

    public HeartbeatTask(ClientManager clientManagerIn) {
        super(clientManagerIn);
        this.clientManager = clientManagerIn;
    }

    @Override
    public void run() {

        if (clientManager.isSocketConnected()) {

            double simpleKey = Math.random();
            //connection.send_direct(new Packet(
                    clientManager.getPluginInstance().getName(), PacketTypes.HEARTBEAT, true, true, new Object[]{simpleKey}));

        }
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onShutdown() {

    }
}
