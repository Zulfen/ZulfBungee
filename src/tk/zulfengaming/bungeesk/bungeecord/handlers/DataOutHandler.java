package tk.zulfengaming.bungeesk.bungeecord.handlers;

import tk.zulfengaming.bungeesk.bungeecord.socket.ServerConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class DataOutHandler implements Runnable {

    private final ServerConnection connection;

    private final Socket socket;

    private final BlockingQueue<Packet> queueOut = new SynchronousQueue<>();

    private final ObjectOutputStream outputStream;


    public DataOutHandler(ServerConnection connectionIn) throws IOException {
        this.connection = connectionIn;

        this.socket = connectionIn.getSocket();
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());

    }


    @Override
    public void run() {
        do {

            try {

                if (socket.isConnected()) {

                    Packet packetOut = queueOut.take();

                    outputStream.writeObject(packetOut);
                    outputStream.flush();

                }

            } catch (IOException | InterruptedException e) {
                connection.getPluginInstance().error("There was an error running the server! Disconnecting");

                connection.end();

                e.printStackTrace();
            }

        } while (connection.isRunning());
    }

    public void disconnect() {

        try {

            if (!socket.isClosed()) {
                outputStream.close();
            }

        } catch (IOException e) {

            connection.getPluginInstance().error("Error closing output stream:");

            e.printStackTrace();
        }

    }

    public void shutdown() {
        disconnect();
    }

    public BlockingQueue<Packet> getQueue() {
        return queueOut;
    }

}
