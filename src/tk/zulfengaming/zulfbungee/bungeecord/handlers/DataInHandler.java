package tk.zulfengaming.zulfbungee.bungeecord.handlers;

import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// issue must be here

public class DataInHandler implements Runnable {

    private final ServerConnection connection;

    private final BlockingQueue<Packet> queueIn = new LinkedBlockingQueue<>();

    private final ObjectInputStream inputStream;


    public DataInHandler(ServerConnection connectionIn) throws IOException {

        this.connection = connectionIn;

        Socket socket = connectionIn.getSocket();
        this.inputStream = new ObjectInputStream(socket.getInputStream());

    }

    @Override
    public void run() {
        do {

            try {

                if (connection.isSocketConnected().get()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {

                        queueIn.put((Packet) dataIn);

                    }

                }

            } catch (SocketException | EOFException e) {
                disconnect();

            } catch (IOException | ClassNotFoundException e) {
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
        return queueIn;
    }

}
