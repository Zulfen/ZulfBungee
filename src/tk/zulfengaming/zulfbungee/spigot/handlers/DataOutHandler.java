package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.*;

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

        do {
            try {

                if (clientListenerManager.isSocketConnected().get()) {

                    Packet packetOut = queueOut.poll(5, TimeUnit.SECONDS);

                    if (packetOut != null) {
                        outputStream.writeObject(packetOut);
                        outputStream.flush();
                    }
                    
                } else {

                    socketBarrier.arriveAndAwaitAdvance();

                    Optional<Socket> socketOptional = clientListenerManager.getSocketHandoff().take();

                    if (clientListenerManager.isTerminated().get()) {

                        socketBarrier.arriveAndDeregister();

                    } else if (socketOptional.isPresent()) {

                        Socket newSocket = socketOptional.get();
                        outputStream = new ObjectOutputStream(newSocket.getOutputStream());

                    }

                }

            } catch (InterruptedException e) {

                socketBarrier.arriveAndDeregister();

            } catch (IOException e) {

                pluginInstance.error("An unexpected error occurred!");
                pluginInstance.error("This likely isn't your fault!");
                pluginInstance.error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                pluginInstance.error("");

                e.printStackTrace();

                clientListenerManager.isSocketConnected().compareAndSet(true, false);

            }

        } while (connection.isRunning().get());

    }

    public LinkedBlockingQueue<Packet> getDataQueue() {
        return queueOut;
    }
}
