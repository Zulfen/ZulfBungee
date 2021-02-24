package tk.zulfengaming.bungeesk.spigot.socket;

import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.spigot.task.HeartbeatTask;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClientConnection  implements Runnable {

    // plugin instance ?
    private final BungeeSkSpigot pluginInstance;

    private final InetAddress clientAddress;
    private final int clientPort;

    private final BukkitTask heartbeatThread;

    // since this is the promise of a socket, if an exception is thrown and we need to shut this
    // bitch down quickly we can
    private Future<Optional<Socket>> futureSocket;

    private Socket socket;

    private final InetAddress serverAddress;
    private final int serverPort;

    private boolean running = true;
    private boolean connected = false;

    // handling packets
    private final PacketHandlerManager packetManager;
    private final SocketHandler socketHandler;

    // direct access to IO
    private ObjectInputStream streamIn;
    private ObjectOutputStream streamOut;

    // thread management
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ClientConnection(BungeeSkSpigot pluginInstance, InetAddress addressIn, int portIn) throws UnknownHostException {
        this.pluginInstance = pluginInstance;

        this.clientAddress = addressIn;
        this.clientPort = portIn;

        this.serverAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("server-address"));
        this.serverPort = pluginInstance.getYamlConfig().getInt("server-port");

        this.packetManager = new PacketHandlerManager(this);
        this.socketHandler = new SocketHandler(this);

        HeartbeatTask heartbeatTask = new HeartbeatTask(this);

        heartbeatThread = pluginInstance.getTaskManager().newRepeatingTask(heartbeatTask, "Heartbeat", pluginInstance.getYamlConfig().getInt("heartbeat-ticks"));

    }

    public void run() {

        do {
            try {

                if (connected) {

                    Optional<Packet> dataIn = read().get(10, TimeUnit.SECONDS);

                    if (dataIn.isPresent()) {

                        Packet packetIn = dataIn.get();

                        if (packetIn.shouldHandle()) {
                            packetManager.handlePacket(packetIn, new InetSocketAddress(serverAddress, serverPort));

                        }
                    }

                } else {
                    futureSocket = connect();

                    Optional<Socket> socketWait = futureSocket.get(5, TimeUnit.SECONDS);

                    pluginInstance.warning("Connection is not available, retrying...");

                    if (socketWait.isPresent()) {

                        socket = socketWait.get();

                        streamOut = new ObjectOutputStream(socket.getOutputStream());
                        streamIn = new ObjectInputStream(socket.getInputStream());

                        connected = true;

                        pluginInstance.log("Connected to the proxy!");
                    }

                }

            } catch (EOFException e) {
                pluginInstance.warning("Proxy disconnected. Retrying...");

                disconnect();

            } catch (IOException | InterruptedException | ExecutionException e) {

                //if (!(e instanceof SocketTimeoutException)) {
                pluginInstance.error("There was an error while handling data from the server!");
                e.printStackTrace();
                //}

                disconnect();


            } catch (TimeoutException e) {

                disconnect();
            }

        } while (running);

    }


    private Future<Optional<Socket>> connect() {
        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
    }

    private void disconnect() {

        try {

            connected = false;

            futureSocket.cancel(true);

            if (!(socket == null)) {
                socket.close();
            }

        } catch (IOException e) {
            pluginInstance.error("There was an error trying to disconnect? Jesus Christ...");
            e.printStackTrace();

        }
    }

    // I fixed it, slightly better now.
    public void shutdown() {
        pluginInstance.log("Shutting down...");

        heartbeatThread.cancel();

        disconnect();

        running = false;

    }

    public Future<Optional<Packet>> read() {

        return pluginInstance.getTaskManager().getExecutorService().submit(() -> {

            pluginInstance.log("read callable is acquiring lock...");
            readWriteLock.readLock().lock();
            pluginInstance.log("read callable released lock");

            try {

                Object objectIn = streamIn.readObject();

                if (objectIn instanceof Packet) {
                    return Optional.of((Packet) objectIn);

                } else {
                    pluginInstance.warning("Packet received from " + serverAddress + ", but does not appear to be valid. Ignoring it.");
                }


            } catch (SocketException ignored) {
                pluginInstance.warning("Proxy disconnected, retrying...");

                disconnect();

            } catch (ClassNotFoundException e) {
                pluginInstance.error("Huge tittums gordon");

            } catch (IOException e) {
                pluginInstance.error("Error reading data:");

                disconnect();

                e.printStackTrace();

            } finally {
                readWriteLock.readLock().unlock();
            }

            return Optional.empty();
        });
    }

    public void send_direct(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "...");

        pluginInstance.log("send_direct is acquiring lock...");
        readWriteLock.writeLock().lock();
        try {

            pluginInstance.log("send_direct unlocked");

            streamOut.writeObject(packetIn);
            streamOut.flush();

        } catch (SocketException ignored) {
            pluginInstance.warning("Proxy disconnected. Retrying...");

            disconnect();

        } catch (IOException e) {
            pluginInstance.error("That packed failed to send :(");
            e.printStackTrace();

        } finally {
            readWriteLock.writeLock().lock();
        }
    }

    public Optional<Packet> send(Packet packetIn)  {

        if (connected) {

            send_direct(packetIn);

            try {
                return read().get(10, TimeUnit.SECONDS);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {

                pluginInstance.error("There was an error sending packet " + packetIn.getType() + ":");
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isConnected() {
        return connected;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public BungeeSkSpigot getPluginInstance() {
        return pluginInstance;
    }
}
