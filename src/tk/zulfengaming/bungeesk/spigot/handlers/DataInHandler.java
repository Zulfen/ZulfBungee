package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientManager;
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

public class DataInHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final ClientManager clientManager;

    private final BlockingQueue<Packet> queueIn = new SynchronousQueue<>();

    private ObjectInputStream inputStream;


    public DataInHandler(ClientManager clientManagerIn, ClientConnection connectionIn) {
        super(clientManagerIn);

        this.clientManager = clientManagerIn;

        this.connection = connectionIn;

    }


    @Override
    public void run() {
        do {
            try {

                if (clientManager.isSocketConnected()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {

                        queueIn.put((Packet) dataIn);

                    }

                } else {

                    Optional<Socket> optionalSocket = clientManager.getSocket();

                    if (optionalSocket.isPresent()) {
                        Socket socket = optionalSocket.get();

                        inputStream = new ObjectInputStream(socket.getInputStream());
                    }

                }

            } catch (IOException | ExecutionException | TimeoutException | InterruptedException | ClassNotFoundException e) {
                clientManager.getPluginInstance().error("There was an error running the server! Disconnecting");

                clientManager.disconnect();
                e.printStackTrace();
            }

        } while (connection.isRunning());
    }

    @Override
    public void onDisconnect() {

        try {
            inputStream.close();

        } catch (IOException e) {

            clientManager.getPluginInstance().error("Error closing input stream:");

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
