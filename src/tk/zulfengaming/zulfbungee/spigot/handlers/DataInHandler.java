package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;

public class DataInHandler extends BukkitRunnable {

    private final ClientConnection connection;

    private final LinkedBlockingQueue<Packet> queueIn = new LinkedBlockingQueue<>();

    private final Phaser socketBarrier;

    private final ClientListenerManager clientListenerManager;
    private final ZulfBungeeSpigot pluginInstance;

    private ObjectInputStream inputStream;

    public DataInHandler(ClientConnection connectionIn) {

        this.connection = connectionIn;
        this.clientListenerManager = connection.getClientListenerManager();
        this.pluginInstance = connection.getPluginInstance();

        this.socketBarrier = clientListenerManager.getSocketBarrier();

        socketBarrier.register();

    }


    @Override
    public void run() {

        Thread.currentThread().setName("DataIn");

        do {
            try {

                if (clientListenerManager.isSocketConnected().get()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {
                        queueIn.put((Packet) dataIn);

                    }

                } else {

                    pluginInstance.logDebug("Thread has arrived: " + Thread.currentThread().getName());

                    socketBarrier.arriveAndAwaitAdvance();

                    Optional<Socket> socketOptional = clientListenerManager.getSocketHandoff().take();

                    if (clientListenerManager.isTerminated().get()) {
                        break;
                    } else if (socketOptional.isPresent()) {
                        Socket newSocket = socketOptional.get();
                        inputStream = new ObjectInputStream(newSocket.getInputStream());
                    }

                }

            } catch (EOFException | SocketException | SocketTimeoutException e) {

                pluginInstance.warning("Proxy server appears to have disconnected!");

                clientListenerManager.isSocketConnected().compareAndSet(true, false);

            } catch (IOException e) {

                pluginInstance.error("An unexpected error occurred!");
                pluginInstance.error("This likely isn't your fault!");
                pluginInstance.error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                pluginInstance.error("");

                e.printStackTrace();

                clientListenerManager.isSocketConnected().compareAndSet(true, false);

            } catch (InterruptedException e) {
                break;
            } catch (ClassNotFoundException e) {
                pluginInstance.error("Packet received was not recognised!");
                e.printStackTrace();
            }

        } while (connection.isRunning().get());

        socketBarrier.arriveAndDeregister();

    }

    public LinkedBlockingQueue<Packet> getDataQueue() {
        return queueIn;
    }

}
