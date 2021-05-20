package tk.zulfengaming.zulfbungee.bungeecord.handlers;

import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataOutHandler implements Runnable {

    private final ServerConnection connection;

    private final Socket socket;

    private final BlockingQueue<Packet> queueOut = new ArrayBlockingQueue<>(10);

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

                if (connection.isSocketConnected().get()) {

                    Packet packetOut = queueOut.take();

                    outputStream.writeObject(packetOut);
                    outputStream.flush();

                }

            } catch (SocketException e) {
                connection.end();

            } catch (IOException e) {
                connection.getPluginInstance().error("There was an error running the server! Disconnecting");

                connection.end();
                e.printStackTrace();

            } catch (InterruptedException ignored) {

            }

        } while (connection.isRunning().get());
    }


    public void shutdown() {
    }

    public BlockingQueue<Packet> getQueue() {
        return queueOut;
    }

}
