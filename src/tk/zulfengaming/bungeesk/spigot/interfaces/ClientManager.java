package tk.zulfengaming.bungeesk.spigot.interfaces;

import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.*;

public class ClientManager {

    private final BungeeSkSpigot pluginInstance;

    private final SocketHandler socketHandler;

    private InetAddress serverAddress;

    private final int serverPort;

    private InetAddress clientAddress;

    private final int clientPort;

    private Socket socket;

    private boolean socketConnected = false;

    private final BlockingQueue<Packet> queueOut = new SynchronousQueue<>();
    private final BlockingQueue<Packet> queueIn = new SynchronousQueue<>();

    private final ArrayList<ClientListener> listeners = new ArrayList<>();

    public ClientManager(BungeeSkSpigot instanceIn) {

        this.pluginInstance = instanceIn;

        try {
            this.serverAddress = InetAddress.getByName(instanceIn.getYamlConfig().getString("server-address"));
            this.clientAddress = InetAddress.getByName(instanceIn.getYamlConfig().getString("client-address"));
        } catch (UnknownHostException e) {

            instanceIn.error("Could not get the name of the host in the config!:");
            e.printStackTrace();

        }
        this.serverPort = pluginInstance.getYamlConfig().getInt("server-port");
        this.clientPort = pluginInstance.getYamlConfig().getInt("client-port");

        this.socketHandler = new SocketHandler(this);

    }

    public synchronized Future<Optional<Socket>> connect() {

        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
    }

    public void disconnect() {

        for (ClientListener listener : listeners) {
            listener.onDisconnect();
        }

        socketConnected = false;

        try {
            socket.close();

        } catch (IOException e) {
            pluginInstance.error("Error trying to closet ClientManager socket!");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        socketConnected = false;

        for (ClientListener listener : listeners) {
            listener.onDisconnect();
            listener.onShutdown();
        }

        listeners.clear();
    }

    public void addListener(ClientListener listener) {
        pluginInstance.log("New listener added: " + listener.getClass().toString());
        listeners.add(listener);
    }

    public BungeeSkSpigot getPluginInstance() {
        return pluginInstance;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public boolean isSocketConnected() {
        return socketConnected;
    }

    public synchronized Optional<Socket> getSocket() throws InterruptedException, ExecutionException, TimeoutException {

        if (socketConnected) {

            return Optional.of(socket);

        } else {
            Optional<Socket> futureSocket = connect().get(5, TimeUnit.SECONDS);

            if (futureSocket.isPresent()) {
                socket = futureSocket.get();
                socketConnected = true;

                return Optional.of(socket);
            }

            return Optional.empty();
        }
    }

    public BlockingQueue<Packet> getQueueOut() {
        return queueOut;
    }

    public BlockingQueue<Packet> getQueueIn() {
        return queueIn;
    }

}
