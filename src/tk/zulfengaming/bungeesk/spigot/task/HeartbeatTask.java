package tk.zulfengaming.bungeesk.spigot.task;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

public class HeartbeatTask extends ClientListener implements Runnable {

    private final ClientListenerManager clientListenerManager;

    public HeartbeatTask(ClientListenerManager clientListenerManagerIn) {
        super(clientListenerManagerIn);
        this.clientListenerManager = clientListenerManagerIn;
    }

    @Override
    public void run() {

        if (clientListenerManager.isSocketConnected()) {

            double simpleKey = Math.random();
            //connection.send_direct(new Packet(
                    clientListenerManager.getPluginInstance().getName(), PacketTypes.HEARTBEAT, true, true, new Object[]{simpleKey}));

        }
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onShutdown() {

    }
}
