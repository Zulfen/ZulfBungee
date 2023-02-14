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
import java.util.concurrent.TimeUnit;

public class DataOutHandler<P> implements Runnable {

    private final BaseServerConnection<P> connection;

    private final BlockingQueue<Packet> queueOut = new LinkedBlockingQueue<>();

    private final ObjectOutputStream outputStream;

    public DataOutHandler(BaseServerConnection<P> connectionIn) throws IOException {
        this.connection = connectionIn;
        this.outputStream = new ObjectOutputStream(connectionIn.getOutputStream());
    }

    @Override
    public void run() {

        do {

            try {

                if (connection.isSocketConnected().get()) {

                    Packet packetOut = queueOut.poll(1, TimeUnit.SECONDS);

                    if (packetOut != null) {
                        outputStream.writeObject(packetOut);
                        outputStream.flush();
                    }

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
