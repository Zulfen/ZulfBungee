package tk.zulfengaming.bungeesk.spigot.interfaces;

import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientListenerManager implements Runnable {

    private final BungeeSkSpigot pluginInstance;

    private final ClientConnection connection;

    private final SocketHandler socketHandler;

    private InetAddress serverAddress;

    private final int serverPort;

    private InetAddress clientAddress;

    private final int clientPort;

    private Socket socket;

    private final Object socketLock = new Object();

    private boolean socketConnected = false;

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

        this.socketHandler = new SocketHandler(this);

    }

    public synchronized Future<Optional<Socket>> connect() {

        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
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

    public void setSocketConnected(boolean socketConnected) {

        this.socketConnected = socketConnected;

        if (!socketConnected) {
            try {
                // If socket was connected and then threw an exception, close it.
                socket.close();
                pluginInstance.warning("Socket closed");
            } catch (IOException e) {
                pluginInstance.error("Failed to close ClientListenerManager socket");
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public Object getSocketLock() {
        return socketLock;
    }

    @Override
    public void run() {

        while (connection.isRunning()) {

            synchronized (socketLock) {

                while (!socketConnected) {
                    pluginInstance.warning("Connection failed. Trying to connect:");

                    try {
                        Optional<Socket> futureSocket = connect().get(5, TimeUnit.SECONDS);

                        if (futureSocket.isPresent()) {
                            socket = futureSocket.get();
                            socketConnected = true;
                        }

                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        e.printStackTrace();
                    }

                }

                socketLock.notifyAll();
            }
        }
    }
}
