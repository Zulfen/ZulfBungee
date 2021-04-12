package tk.zulfengaming.bungeesk.bungeecord.handlers;

import tk.zulfengaming.bungeesk.bungeecord.socket.ServerConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class DataInHandler implements Runnable {

    private final ServerConnection connection;

    private final Socket socket;

    private final BlockingQueue<Packet> queueIn = new SynchronousQueue<>();

    private final ObjectInputStream inputStream;


    public DataInHandler(ServerConnection connectionIn) throws IOException {
        this.connection = connectionIn;

        this.socket = connectionIn.getSocket();
        this.inputStream = new ObjectInputStream(socket.getInputStream());

    }


    @Override
    public void run() {
        do {

            try {

                if (socket.isConnected()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {

                        queueIn.put((Packet) dataIn);

                    }

                }

            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                connection.getPluginInstance().error("There was an error running the server! Disconnecting");

                connection.disconnect();

                e.printStackTrace();
            }

        } while (connection.isRunning());
    }

    public void disconnect() {

        try {
            inputStream.close();

        } catch (IOException e) {

            connection.getPluginInstance().error("Error closing input stream:");

            e.printStackTrace();
        }

    }

    public void shutdown() {
        disconnect();
    }

    public BlockingQueue<Packet> getQueue() {
        return queueIn;
    }

}
