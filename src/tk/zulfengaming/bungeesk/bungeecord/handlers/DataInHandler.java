package tk.zulfengaming.bungeesk.bungeecord.handlers;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeoutException;

public class DataInHandler implements Runnable, Clie {

    private final ClientConnection connection;

    private final ClientListenerManager clientListenerManager;

    private final BlockingQueue<Packet> queueIn = new SynchronousQueue<>();

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

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {

                        queueIn.put((Packet) dataIn);

                    }

                } else {

                    Optional<Socket> optionalSocket = clientListenerManager.getSocket();

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
            inputStream.close();

        } catch (IOException e) {

            clientListenerManager.getPluginInstance().error("Error closing input stream:");

            e.printStackTrace();
        }

    }

    @Override
    public void onShutdown() {

    }

    public BlockingQueue<Packet> getQueue() {
        return queueIn;
    }

}
