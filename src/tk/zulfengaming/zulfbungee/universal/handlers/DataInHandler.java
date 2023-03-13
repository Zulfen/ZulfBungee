package tk.zulfengaming.zulfbungee.universal.handlers;

import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;

// issue must be here

public class DataInHandler<P> implements Runnable {

    private final BaseServerConnection<P> connection;

    private final LinkedBlockingDeque<Optional<Packet>> queueIn = new LinkedBlockingDeque<>();

    private final ObjectInputStream inputStream;


    public DataInHandler(BaseServerConnection<P> connectionIn, Socket socketIn) throws IOException {
        this.connection = connectionIn;
        this.inputStream = new ObjectInputStream(socketIn.getInputStream());
    }

    @Override
    public void run() {

        do {

            try {

                if (connection.isSocketConnected().get()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {
                        queueIn.putLast(Optional.of((Packet) dataIn));
                    }

                }

            } catch (InterruptedException | SocketException | EOFException e) {

                disconnect();

            } catch (IOException | ClassNotFoundException e) {
                connection.getPluginInstance().error(String.format("There was an error handling data in the connection to address %s:", connection.getAddress()));
                e.printStackTrace();
                disconnect();
            }


        } while (connection.isRunning().get());
    }

    public void disconnect() {

        try {
            queueIn.putLast(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        connection.end();

    }

    public void shutdown() {
        try {
            queueIn.putLast(Optional.empty());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public LinkedBlockingDeque<Optional<Packet>> getQueue() {
        return queueIn;
    }

}
