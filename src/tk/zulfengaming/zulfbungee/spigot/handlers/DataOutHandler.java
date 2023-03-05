package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class DataOutHandler extends BukkitRunnable {

    private final SocketConnection connection;

    private final LinkedBlockingQueue<Optional<Packet>> queueOut = new LinkedBlockingQueue<>();

    private final ObjectOutputStream outputStream;

    private final ZulfBungeeSpigot pluginInstance;

    public DataOutHandler(SocketConnection connectionIn) throws IOException {
        this.connection = connectionIn;
        this.pluginInstance = connection.getPluginInstance();
        this.outputStream = new ObjectOutputStream(connectionIn.getOutputStream());
    }


    @Override
    public void run() {

        Thread.currentThread().setName("DataOut");

        do {
            try {

                if (connection.isConnected().get()) {

                    Optional<Packet> packetOut = queueOut.take();

                    if (packetOut.isPresent()) {
                        outputStream.writeObject(packetOut.get());
                        outputStream.flush();
                    }

                }

            } catch (InterruptedException e) {
                break;

            } catch (EOFException | SocketException | SocketTimeoutException e) {

                pluginInstance.warning("Proxy server appears to have disconnected!");

                connection.isConnected().compareAndSet(true, false);

            } catch (IOException e) {

                pluginInstance.error("An unexpected error occurred!");
                pluginInstance.error("This likely isn't your fault!");
                pluginInstance.error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                pluginInstance.error("");

                e.printStackTrace();

                connection.isConnected().compareAndSet(true, false);
                connection.shutdown();

            }

        } while (connection.isRunning().get());

    }

    public void disconnect() {
        connection.isConnected().compareAndSet(true, false);
        queueOut.offer(Optional.empty());
    }

    public void shutdown() {
        disconnect();
    }

    public LinkedBlockingQueue<Optional<Packet>> getDataQueue() {
        return queueOut;
    }
}
