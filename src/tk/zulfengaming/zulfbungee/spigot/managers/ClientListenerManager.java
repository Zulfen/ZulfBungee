package tk.zulfengaming.zulfbungee.spigot.managers;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.config.YamlConfig;
import tk.zulfengaming.zulfbungee.spigot.handlers.SocketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientListenerManager extends BukkitRunnable {

    private final ZulfBungeeSpigot pluginInstance;

    private final ClientConnection connection;

    private final SocketHandler socketHandler;

    private InetAddress serverAddress;

    private InetAddress clientAddress;

    private final TransferQueue<Optional<Socket>> socketHandoff = new LinkedTransferQueue<>();

    private final Phaser socketBarrier = new Phaser();

    private Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(false);
    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private ClientInfo zulfServerInfo;

    public ClientListenerManager(ClientConnection connectionIn) {

        this.connection = connectionIn;
        this.pluginInstance = connectionIn.getPluginInstance();

        YamlConfig config = pluginInstance.getYamlConfig();

        try {

            this.serverAddress = InetAddress.getByName(config.getString("server-host"));
            this.clientAddress = InetAddress.getByName(config.getString("client-host"));

        } catch (UnknownHostException e) {

            pluginInstance.error("Could not get the name of the host in the config!:");
            e.printStackTrace();

        }

        int serverPort = config.getInt("server-port");
        int clientPort = config.getInt("client-port");

        this.socketHandler = new SocketHandler(clientAddress, clientPort, serverAddress, serverPort, connection.getTimeout());

        socketBarrier.register();

    }

    private void closeSocket() {

        if (socket != null) {

            try {

                if (!socket.isClosed()) {
                    socket.close();
                }

            } catch (IOException e) {
                pluginInstance.error("Error closing client socket:");
                e.printStackTrace();
            }
        }

    }

    public void shutdown() {
        if (socketConnected.compareAndSet(true, false) && terminated.compareAndSet(false, true)) {
            closeSocket();
        }
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public AtomicBoolean isSocketConnected() {
        return socketConnected;
    }

    public AtomicBoolean isTerminated() {
        return terminated;
    }

    public Socket getSocket() {
        return socket;
    }

    public TransferQueue<Optional<Socket>> getSocketHandoff() {
        return socketHandoff;
    }

    public Phaser getSocketBarrier() {
        return socketBarrier;
    }

    public ClientInfo getClientInfo() {
        return zulfServerInfo;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("ClientListenerManager");

        while (connection.isRunning().get()) {

            pluginInstance.logDebug("Thread has arrived: " + Thread.currentThread().getName());

            socketBarrier.arriveAndAwaitAdvance();

            if (!terminated.get()) {
                pluginInstance.warning("Connection lost with proxy, attempting to connect every 2 seconds...");
            }

            closeSocket();

            boolean gotSocket = false;

            do {

                try {

                    if (!terminated.get()) {

                        // blocking call
                        Optional<Socket> socketOptional = pluginInstance.getTaskManager().submitCallable(socketHandler);
                        socketOptional.ifPresent(value -> socket = value);

                        if (socket != null) {
                            gotSocket = true;
                        }

                    } else {
                        pluginInstance.logDebug(ChatColor.RED + "Done!");
                        break;
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (RejectedExecutionException ignored) {
                    // ignored as we specifically throw this exception upon shutting down, we don't need to do any more work
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof InterruptedException) {
                        break;
                    }
                    pluginInstance.logDebug(ChatColor.RED + String.format("Error while creating socket: %s", e.getCause().getMessage()));
                }

            } while (!gotSocket);

            if (gotSocket) {

                socketConnected.compareAndSet(false, true);

                while (socketHandoff.hasWaitingConsumer()) {
                    socketHandoff.tryTransfer(Optional.of(socket));
                }

                pluginInstance.logInfo(ChatColor.GREEN + "Connection established with proxy!");

                zulfServerInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());

                connection.sendDirect(new Packet(PacketTypes.PROXY_CLIENT_INFO, true, true, zulfServerInfo));
                connection.sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, true, true, new Object[0]));

            } else {

                while (socketHandoff.hasWaitingConsumer()) {
                    socketHandoff.tryTransfer(Optional.empty());
                }

            }
        }

        socketBarrier.arriveAndDeregister();

    }
}
