package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ServerConnectionHandler implements Runnable {

    private boolean isConnected;

    private ClientConnection connection;

    public ServerConnectionHandler(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        try {

            if (!connection.socket.isConnected()) {

                running = false;

                connection.instance.log("Connecting to proxy...");

                connection.socket.connect(serverSocketAddress, 5000);
            }

        } catch (SocketTimeoutException | SocketException | UnknownHostException e) {
            connection.instance.warning("There was an error connecting. Retrying...");

        } catch (IOException e) {
            connection.instance.error("Error connecting:");
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}

