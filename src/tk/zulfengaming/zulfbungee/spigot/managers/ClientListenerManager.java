package tk.zulfengaming.zulfbungee.spigot.managers;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.SocketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ClientInfo;

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

    private final Phaser socketBarrier = new Phaser(1);

    private Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(false);
    private final AtomicBoolean terminated = new AtomicBoolean(false);

    private ClientInfo clientInfo;


    public ClientListenerManager(ClientConnection connectionIn) {

        this.connection = connectionIn;
        this.pluginInstance = connectionIn.getPluginInstance();

        try {

            this.serverAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("server-host"));
            this.clientAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("client-host"));

        } catch (UnknownHostException e) {

            pluginInstance.error("Could not get the name of the host in the config!:");
            e.printStackTrace();

        }

        int serverPort = pluginInstance.getYamlConfig().getInt("server-port");
        int clientPort = pluginInstance.getYamlConfig().getInt("client-port");

        this.socketHandler = new SocketHandler(clientAddress, clientPort, serverAddress, serverPort, connection.getTimeout());

    }

    public void shutdown() {
        socketConnected.compareAndSet(true, false);
        terminated.compareAndSet(false, true);
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
        return clientInfo;
    }


    @Override
    public void run() {

        while (connection.isRunning().get()) {

            socketBarrier.arriveAndAwaitAdvance();

            pluginInstance.warning("Connection lost with proxy, attempting to connect every 2 seconds...");

            if (socket != null && !socketConnected.get()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    pluginInstance.error("Error closing client socket:");
                    e.printStackTrace();
                }
            }

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
                        socketBarrier.arriveAndDeregister();
                        break;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    socketBarrier.arriveAndDeregister();
                    break;
                } catch (ExecutionException e) {
                    pluginInstance.error(String.format("Error while creating socket: %s", e.getCause()));
                }

            } while (!gotSocket);

            if (gotSocket) {

                socketConnected.compareAndSet(false, true);

                while (socketHandoff.hasWaitingConsumer()) {
                    socketHandoff.tryTransfer(Optional.of(socket));
                }

                pluginInstance.logInfo(ChatColor.GREEN + "Connection established with proxy!");

                clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());

                connection.send_direct(new Packet(PacketTypes.PROXY_CLIENT_INFO, true, true, clientInfo));
                connection.send_direct(new Packet(PacketTypes.GLOBAL_SCRIPT, true, true, new Object[0]));

            } else {

                while (socketHandoff.hasWaitingConsumer()) {
                    socketHandoff.tryTransfer(Optional.empty());
                }

            }
        }
    }
}
