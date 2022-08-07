package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class DataOutHandler extends BukkitRunnable {

    private final ClientConnection connection;

    private final LinkedBlockingQueue<Packet> queueOut = new LinkedBlockingQueue<>();

    private ObjectOutputStream outputStream;

    private final Phaser socketBarrier;

    private final ClientListenerManager clientListenerManager;
    private final ZulfBungeeSpigot pluginInstance;

    public DataOutHandler(ClientConnection connectionIn) {

        this.connection = connectionIn;
        this.clientListenerManager = connection.getClientListenerManager();
        this.pluginInstance = connection.getPluginInstance();

        this.socketBarrier = clientListenerManager.getSocketBarrier();

        socketBarrier.register();

    }


    @Override
    public void run() {

        Thread.currentThread().setName("DataOut");

        do {
            try {

                if (clientListenerManager.isSocketConnected().get()) {

                    Packet packetOut = queueOut.poll(1, TimeUnit.SECONDS);

                    if (packetOut != null) {
                        outputStream.writeObject(packetOut);
                        outputStream.flush();
                    }
                    
                } else {

                    pluginInstance.logDebug("Thread has arrived: " + Thread.currentThread().getName());

                    socketBarrier.arriveAndAwaitAdvance();

                    Optional<Socket> socketOptional = clientListenerManager.getSocketHandoff().take();

                    if (clientListenerManager.isTerminated().get()) {
                        break;
                    } else if (socketOptional.isPresent()) {
                        Socket newSocket = socketOptional.get();
                        outputStream = new ObjectOutputStream(newSocket.getOutputStream());
                    }

                }

            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {

                pluginInstance.error("An unexpected error occurred!");
                pluginInstance.error("This likely isn't your fault!");
                pluginInstance.error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                pluginInstance.error("");

                e.printStackTrace();

                clientListenerManager.isSocketConnected().compareAndSet(true, false);

            }

        } while (connection.isRunning().get());

        socketBarrier.arriveAndDeregister();

    }

    public LinkedBlockingQueue<Packet> getDataQueue() {
        return queueOut;
    }
}
