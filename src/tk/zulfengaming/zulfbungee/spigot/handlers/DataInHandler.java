package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;

public class DataInHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final BlockingQueue<Packet> queueIn = new ArrayBlockingQueue<>(10);

    private final Phaser socketBarrier;

    private ObjectInputStream inputStream;

    public DataInHandler(ClientListenerManager clientListenerManagerIn, ClientConnection connectionIn) {
        super(clientListenerManagerIn);

        this.connection = connectionIn;

        this.socketBarrier = clientListenerManagerIn.getSocketBarrier();

        socketBarrier.register();

    }


    @Override
    public void run() {

        do {
            try {

                if (getClientListenerManager().isSocketConnected().get()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {
                        queueIn.put((Packet) dataIn);

                    }

                } else {

                    socketBarrier.arriveAndAwaitAdvance();

                    Socket newSocket = getClientListenerManager().getSocketHandoff().take();

                    inputStream = new ObjectInputStream(newSocket.getInputStream());


                }

            } catch (EOFException | SocketException | SocketTimeoutException e) {
                getClientListenerManager().getPluginInstance().warning("Proxy server appears to have disconnected!");

                getClientListenerManager().isSocketConnected().compareAndSet(true, false);

            } catch (IOException e) {
                getClientListenerManager().getPluginInstance().error("An unexpected error occurred!");
                getClientListenerManager().getPluginInstance().error("This likely isn't your fault!");
                getClientListenerManager().getPluginInstance().error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                getClientListenerManager().getPluginInstance().error("");

                e.printStackTrace();

                getClientListenerManager().isSocketConnected().compareAndSet(true, false);

            } catch (InterruptedException ignored) {


            } catch (ClassNotFoundException e) {
                getClientListenerManager().getPluginInstance().error("Packet received was not recognised!");

                e.printStackTrace();
            }

        } while (connection.isRunning().get());

        socketBarrier.arriveAndDeregister();
    }

    public BlockingQueue<Packet> getDataQueue() {
        return queueIn;
    }

}
