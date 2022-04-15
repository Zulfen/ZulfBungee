package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketHandler extends ClientListener implements Runnable {

    private final int timeout;

    public SocketHandler(ClientListenerManager clientListenerManagerIn) {
        super(clientListenerManagerIn);

        this.timeout = (int) Math.ceil((getClientListenerManager().getConnection().getHeartbeatTicks() / 20f) * 1000);
    }

    @Override
    public void run() {

        Socket socket = new Socket();

        try {

            socket.setReuseAddress(true);
            socket.setSoTimeout(timeout);

            socket.bind(new InetSocketAddress(getClientListenerManager().getClientAddress(), getClientListenerManager().getClientPort()));
            socket.connect(new InetSocketAddress(getClientListenerManager().getServerAddress(), getClientListenerManager().getServerPort()));

            getClientListenerManager().getSocketWaitQueue().put(socket);

        } catch (IOException connect) {

            try {
                socket.close();
            } catch (IOException closing) {
                getClientListenerManager().getPluginInstance().error("Error closing unused socket:");
                closing.printStackTrace();
            }

        } catch (InterruptedException ignored) {

        }

    }
}

