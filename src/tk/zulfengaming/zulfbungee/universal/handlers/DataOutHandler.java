package tk.zulfengaming.zulfbungee.universal.handlers;

import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class DataOutHandler<P> implements Runnable {

    private final BaseServerConnection<P> connection;

    private final LinkedBlockingQueue<Optional<Packet>> queueOut = new LinkedBlockingQueue<>();

    private final ObjectOutputStream outputStream;

    public DataOutHandler(BaseServerConnection<P> connectionIn, Socket socketIn) throws IOException {
        this.connection = connectionIn;
        this.outputStream = new ObjectOutputStream(socketIn.getOutputStream());
    }

    @Override
    public void run() {

        do {

            try {

                if (connection.isSocketConnected().get()) {

                    Optional<Packet> packetOut = queueOut.take();

                    if (packetOut.isPresent()) {
                        outputStream.writeObject(packetOut.get());
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

        try {
            queueOut.put(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        connection.end();

    }

    public void shutdown() {

        try {
            queueOut.put(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }

    public LinkedBlockingQueue<Optional<Packet>> getQueue() {
        return queueOut;
    }

}
