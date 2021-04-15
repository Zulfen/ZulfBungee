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

                    clientListenerManager.disconnect();

                    // TODO: fix me getting a closed socket, and fix exceptions!

                    Optional<Socket> optionalSocket = clientListenerManager.getSocket();
                    clientListenerManager.getPluginInstance().log("DataIn requested socket!");

                    if (optionalSocket.isPresent()) {
                        Socket socket = optionalSocket.get();

                        inputStream = new ObjectInputStream(socket.getInputStream());
                    }

                }

            } catch (IOException | ExecutionException | TimeoutException | ClassNotFoundException e) {
                clientListenerManager.getPluginInstance().error("There was an error running the server! Disconnecting");

                clientListenerManager.disconnect();
                e.printStackTrace();

            } catch (InterruptedException ignored) {

            }

        } while (connection.isRunning());
    }

    @Override
    public void onDisconnect() {

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
