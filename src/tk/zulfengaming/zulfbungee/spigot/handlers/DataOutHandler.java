package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;

public class DataOutHandler extends BukkitRunnable {

    private final SocketConnection connection;

    private final LinkedBlockingDeque<Optional<Packet>> queueOut = new LinkedBlockingDeque<>();

    private final ObjectOutputStream outputStream;

    private final ZulfBungeeSpigot pluginInstance;

    public DataOutHandler(SocketConnection connectionIn, Socket socketIn) throws IOException {
        this.connection = connectionIn;
        this.pluginInstance = connection.getPluginInstance();
        this.outputStream = new ObjectOutputStream(socketIn.getOutputStream());
    }


    @Override
    public void run() {

        Thread.currentThread().setName(String.format("DataOut@%s", connection.getAddress()));

        do {
            try {

                if (connection.isConnected().get()) {

                    Optional<Packet> packetOut = queueOut.takeLast();

                    if (packetOut.isPresent()) {
                        outputStream.writeObject(packetOut.get());
                        outputStream.flush();
                    }

                }

            } catch (InterruptedException e) {
                break;

            } catch (EOFException | SocketException | SocketTimeoutException e) {

                pluginInstance.warning("Proxy server appears to have disconnected!");

                connection.shutdown();

            } catch (IOException e) {

                pluginInstance.error("An unexpected error occurred!");
                pluginInstance.error("This likely isn't your fault!");
                pluginInstance.error("Please report this by making an issue on GitHub or contacting one of the devs so we can fix this issue!");
                pluginInstance.error("");

                e.printStackTrace();

                connection.shutdown();

            }

        } while (connection.isRunning().get());

    }

    public void disconnect() {
        queueOut.offerLast(Optional.empty());
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing output stream", e);
        }
    }

    public void shutdown() {
        disconnect();
    }

    public LinkedBlockingDeque<Optional<Packet>> getDataQueue() {
        return queueOut;
    }
}
