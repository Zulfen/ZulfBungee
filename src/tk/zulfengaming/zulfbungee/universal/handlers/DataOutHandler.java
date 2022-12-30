package tk.zulfengaming.zulfbungee.universal.handlers;

import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataOutHandler implements Runnable {

    private final BaseServerConnection connection;

    private final BlockingQueue<Packet> queueOut = new LinkedBlockingQueue<>();

    private final ObjectOutputStream outputStream;

    public DataOutHandler(BaseServerConnection connectionIn) throws IOException {
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

            } catch (InterruptedException | SocketException | EOFException e) {
                disconnect();

            } catch (IOException e) {

                connection.getPluginInstance().error(String.format("There was an error handling data in the connection to address %s:", connection.getAddress()));
                e.printStackTrace();
                disconnect();

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
