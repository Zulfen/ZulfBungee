package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class SocketHandler extends ClientListener implements Callable<Socket> {

    private final int timeout;

    public SocketHandler(ClientListenerManager clientListenerManagerIn) {
        super(clientListenerManagerIn);

        this.timeout = (int) Math.ceil((getClientListenerManager().getConnection().getHeartbeatTicks() / 20f) * 1000);
    }

    @Override
    public Socket call() throws InterruptedException {

        // Fixes potential memory leak

        Socket socket = new Socket();
        boolean connected = false;

        while (!connected) {

            try {

                socket.setReuseAddress(true);
                socket.setSoTimeout(timeout);

                socket.bind(new InetSocketAddress(getClientListenerManager().getClientAddress(), getClientListenerManager().getClientPort()));
                socket.connect(new InetSocketAddress(getClientListenerManager().getServerAddress(), getClientListenerManager().getServerPort()));

                connected = true;

            } catch (IOException connect) {

                try {
                    socket.close();
                } catch (IOException closing) {
                    getClientListenerManager().getPluginInstance().error("Error closing unused socket:");
                    closing.printStackTrace();

                }

                // gives it time between connection attempts
                Thread.sleep(2000);
            }

        }

        return socket;

    }
}

