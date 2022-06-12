package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientListenerManager implements Runnable {

    private final ZulfBungeeSpigot pluginInstance;

    private final ClientConnection connection;

    private final SocketHandler socketHandler;

    private InetAddress serverAddress;

    private final int serverPort;

    private InetAddress clientAddress;

    private final int clientPort;

    private final SynchronousQueue<Socket> socketRetrieve = new SynchronousQueue<>();
    private final TransferQueue<Socket> socketHandoff = new LinkedTransferQueue<>();
    private final Phaser socketBarrier = new Phaser();

    private Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(false);

    private ServerInfo serverInfo;

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

        this.serverPort = pluginInstance.getYamlConfig().getInt("server-port");
        this.clientPort = pluginInstance.getYamlConfig().getInt("client-port");

        socketBarrier.register();

        this.socketHandler = new SocketHandler(this);

    }

    private BukkitTask connect() throws ExecutionException, InterruptedException {
        return pluginInstance.getTaskManager().newRepeatingTask(socketHandler, "SocketHandler", 40);
    }

    public void shutdown() throws IOException {

        socketBarrier.arriveAndDeregister();

        if (socket != null) {
            socket.close();
        }

    }

    public void addListener(ClientListener listener) {
        pluginInstance.logDebug("New listener added: " + listener.getClass().toString());
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public AtomicBoolean isSocketConnected() {
        return socketConnected;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public SynchronousQueue<Socket> getSocketRetrieve() {
        return socketRetrieve;
    }

    public TransferQueue<Socket> getSocketHandoff() {
        return socketHandoff;
    }

    public Phaser getSocketBarrier() {
        return socketBarrier;
    }

    public ServerInfo getClientInfo() {
        return serverInfo;
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

            try {

                BukkitTask socketAwaitTask = connect();

                socket = socketRetrieve.take();

                socketAwaitTask.cancel();

                socketConnected.compareAndSet(false, true);

                while (socketHandoff.hasWaitingConsumer()) {
                    socketHandoff.transfer(socket);
                }

                pluginInstance.logInfo(ChatColor.GREEN + "Connection established with proxy!");

                serverInfo = new ServerInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());

                connection.send_direct(new Packet(PacketTypes.PROXY_SERVER_INFO, true, true, serverInfo));
                connection.send_direct(new Packet(PacketTypes.GLOBAL_SCRIPT, true, true, null));

            } catch (InterruptedException ignored) {

            } catch (ExecutionException e) {
                pluginInstance.error("Error during the socket getting process!");
                e.printStackTrace();
            }

        }

    }
}
