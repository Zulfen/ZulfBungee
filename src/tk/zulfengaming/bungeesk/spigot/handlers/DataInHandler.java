package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.*;

public class DataInHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final ClientListenerManager clientListenerManager;

    private final BlockingQueue<Packet> queueIn = new ArrayBlockingQueue<>(10);

    private ObjectInputStream inputStream;


    public DataInHandler(ClientListenerManager clientListenerManagerIn, ClientConnection connectionIn) {
        super(clientListenerManagerIn);

        this.clientListenerManager = clientListenerManagerIn;

        this.connection = connectionIn;

    }


    @Override
    public void run() {
        do {
            try {

                if (clientListenerManager.isSocketConnected()) {

                    clientListenerManager.getPluginInstance().log("DataOut connected!:");
                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {

                        queueIn.put((Packet) dataIn);

                    }

                } else {

                    // TODO: fix me getting a closed socket, and fix exceptions!

                    Optional<Socket> optionalSocket = clientListenerManager.getSocket();
                    clientListenerManager.getPluginInstance().log("DataIn requested socket!");

                    if (optionalSocket.isPresent()) {
                        Socket socket = optionalSocket.get();

                        inputStream = new ObjectInputStream(socket.getInputStream());
                    }

                }

            } catch (IOException | ExecutionException | TimeoutException | InterruptedException | ClassNotFoundException e) {
                clientListenerManager.getPluginInstance().error("There was an error running the server! Disconnecting");

                clientListenerManager.disconnect();
                e.printStackTrace();
            }

        } while (connection.isRunning());
    }

    @Override
    public void onDisconnect() {

        try {

            if (connection.isConnected()) {
                inputStream.close();
            }

        } catch (IOException e) {

            clientListenerManager.getPluginInstance().error("Error closing input stream:");

            e.printStackTrace();
        }

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public void onConnect() {

    }

    public BlockingQueue<Packet> getQueue() {
        return queueIn;
    }

}
