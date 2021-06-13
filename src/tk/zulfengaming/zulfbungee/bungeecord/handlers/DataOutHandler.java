package tk.zulfengaming.zulfbungee.bungeecord.handlers;

import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class DataOutHandler implements Runnable {

    private final ServerConnection connection;

    private final BlockingQueue<Packet> queueOut = new SynchronousQueue<>();

    private final ObjectOutputStream outputStream;

    public DataOutHandler(ServerConnection connectionIn) throws IOException {
        this.connection = connectionIn;

        Socket socket = connectionIn.getSocket();
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

            } catch (SocketException | EOFException e) {
                disconnect();

            } catch (IOException e) {
                connection.getPluginInstance().error("There was an error running the server! Disconnecting");

                disconnect();
                e.printStackTrace();

            } catch (InterruptedException ignored) {

            }

        } while (connection.isRunning().get());
    }

    public void disconnect() {

        if (connection.isSocketConnected().compareAndSet(true, false)) {
            connection.end();
        }

    }

    public void shutdown() {
        disconnect();
    }

    public BlockingQueue<Packet> getQueue() {
        return queueOut;
    }

}
