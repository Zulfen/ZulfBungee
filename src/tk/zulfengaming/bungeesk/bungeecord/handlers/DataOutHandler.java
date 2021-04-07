package tk.zulfengaming.bungeesk.bungeecord.handlers;

import tk.zulfengaming.bungeesk.bungeecord.socket.ServerConnection;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeoutException;

public class DataOutHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final ClientListenerManager clientListenerManager;

    private final BlockingQueue<Packet> queueOut = new SynchronousQueue<>();

    private ObjectOutputStream outputStream;

    public DataOutHandler(ServerConnection) {
        super(clientManagerIn);

        this.connection = connectionIn;
        this.clientListenerManager = clientManagerIn;

    }


    @Override
    public void run() {
        do {
            try {

                if (clientListenerManager.isSocketConnected()) {

                    Packet packetOut = queueOut.take();

                    outputStream.writeObject(packetOut);
                    outputStream.flush();

                } else {

                    Optional<Socket> optionalSocket = clientListenerManager.getSocket();
                    clientListenerManager.getPluginInstance().log("DataOut requested socket!");

                    if (optionalSocket.isPresent()) {
                        Socket socket = optionalSocket.get();

                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                    }

                }

            } catch (IOException | ExecutionException | TimeoutException | InterruptedException e) {
                clientListenerManager.getPluginInstance().error("There was an error running the server! Disconnecting");

                clientListenerManager.disconnect();
                e.printStackTrace();
            }

        } while (connection.isRunning());
    }

    @Override
    public void onDisconnect() {

        try {
            outputStream.close();

        } catch (IOException e) {

            clientListenerManager.getPluginInstance().error("Error closing input stream:");

            e.printStackTrace();
        }

    }

    @Override
    public void onShutdown() {

    }

    public BlockingQueue<Packet> getQueue() {
        return queueOut;
    }
}
