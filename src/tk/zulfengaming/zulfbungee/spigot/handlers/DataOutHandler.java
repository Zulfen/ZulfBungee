package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;

public class DataOutHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final BlockingQueue<Packet> queueOut = new SynchronousQueue<>();

    private ObjectOutputStream outputStream;

    private final Phaser socketBarrier;

    public DataOutHandler(ClientListenerManager clientListenerManagerIn, ClientConnection connectionIn) {
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
                    Packet packetOut = queueOut.poll(5, TimeUnit.SECONDS);

                    if (packetOut != null) {
                        outputStream.writeObject(packetOut);
                        outputStream.flush();
                    }

                } else {

                    socketBarrier.arriveAndAwaitAdvance();

                    Socket newSocket = getClientListenerManager().getSocketHandoff().take();

                    outputStream = new ObjectOutputStream(newSocket.getOutputStream());

                }

            } catch (SocketException e) {

                getClientListenerManager().isSocketConnected().compareAndSet(true, false);

            } catch (IOException e) {

                getClientListenerManager().getPluginInstance().error("An unexpected error occurred!");
                getClientListenerManager().getPluginInstance().error("This likely isn't your fault!");
                getClientListenerManager().getPluginInstance().error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                getClientListenerManager().getPluginInstance().error("");

                e.printStackTrace();

                getClientListenerManager().isSocketConnected().compareAndSet(true, false);

            } catch (InterruptedException ignored) {
            }

        } while (connection.isRunning().get());

        socketBarrier.arriveAndDeregister();
    }

    public BlockingQueue<Packet> getDataQueue() {
        return queueOut;
    }
}
