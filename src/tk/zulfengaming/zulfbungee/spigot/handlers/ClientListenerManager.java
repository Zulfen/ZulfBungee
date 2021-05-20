package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.ChatColor;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;
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

    private final TransferQueue<Socket> socketHandoff = new LinkedTransferQueue<>();
    private final Phaser socketBarrier = new Phaser();

    private volatile Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(false);

    private final ArrayList<ClientListener> listeners = new ArrayList<>();

    public ClientListenerManager(ClientConnection connectionIn) {

        this.connection = connectionIn;
        this.pluginInstance = connectionIn.getPluginInstance();

        try {
            this.serverAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("server-address"));
            this.clientAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("client-address"));
        } catch (UnknownHostException e) {

            pluginInstance.error("Could not get the name of the host in the config!:");
            e.printStackTrace();

        }
        this.serverPort = pluginInstance.getYamlConfig().getInt("server-port");
        this.clientPort = pluginInstance.getYamlConfig().getInt("client-port");

        socketBarrier.register();

        this.socketHandler = new SocketHandler(this);

    }

    private Future<Optional<Socket>> connect() {

        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
    }


    public void shutdown() {

        listeners.clear();
    }

    public void addListener(ClientListener listener) {
        pluginInstance.logDebug("New listener added: " + listener.getClass().toString());
        listeners.add(listener);
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
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

    public BlockingQueue<Socket> getSocketHandoff() {
        return socketHandoff;
    }

    public Phaser getSocketBarrier() {
        return socketBarrier;
    }

    public ArrayList<ClientListener> getListeners() {
        return listeners;
    }

    @Override
    public void run() {

        while (connection.isRunning().get()) {

            socketBarrier.arriveAndAwaitAdvance();

            if (socket != null && !socketConnected.get()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    pluginInstance.error("Error closing client socket:");
                    e.printStackTrace();
                }
            }

            while (!socketConnected.get()) {

                pluginInstance.warning("Not connected to the proxy! Trying to connect...");

                try {
                    Optional<Socket> futureSocket = connect().get(5, TimeUnit.SECONDS);

                    if (futureSocket.isPresent()) {

                        socket = futureSocket.get();

                        while (socketHandoff.hasWaitingConsumer()) {
                            socketHandoff.transfer(socket);
                        }

                        socketConnected.set(true);

                        pluginInstance.logInfo(ChatColor.GREEN + "Connection established with proxy!");
                        connection.send_direct(new Packet(PacketTypes.CLIENT_HANDSHAKE, false, true, connection.getServerName()));
                    }

                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    pluginInstance.error("Error getting socket:");
                    e.printStackTrace();
                }
            }
        }
    }
}
