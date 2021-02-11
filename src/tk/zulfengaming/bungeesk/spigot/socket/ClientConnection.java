package tk.zulfengaming.bungeesk.spigot.socket;

import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ClientConnection implements Runnable {

    public static ClientConnection clientConnection;


    // plugin instance ?
    private final BungeeSkSpigot pluginInstance;

    private final InetAddress clientAddress;
    private final int clientPort;

    private Socket socket;

    private final InetAddress serverAddress;
    private final int serverPort;

    private boolean running = true;
    private boolean connected = false;

    // handling packets
    private final PacketHandlerManager packetManager;
    private final SocketHandler socketHandler;

    // direct access to IO
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;

    public ClientConnection(BungeeSkSpigot pluginInstance, InetAddress addressIn, int portIn) throws UnknownHostException {
        this.pluginInstance = pluginInstance;

        this.clientAddress = addressIn;
        this.clientPort = portIn;

        this.serverAddress = InetAddress.getByName(pluginInstance.getYamlConfig().getString("server-address"));
        this.serverPort = pluginInstance.getYamlConfig().getInt("server-port");

        this.packetManager = new PacketHandlerManager(this);
        this.socketHandler = new SocketHandler(this);

    }

    public void run() {
        do {

            try {

                if (connected) {

                    if (read().isPresent()) {

                        Packet packetIn = read().get();

                        Packet processedPacket = packetManager.handlePacket(packetIn, new InetSocketAddress(serverAddress, serverPort));

                        if (!(processedPacket == null) && packetIn.isReturnable()) {
                            send_direct(processedPacket);

                        }
                    }

                } else {
                    Optional<Socket> futureSocket = connect().get(5, TimeUnit.SECONDS);

                    pluginInstance.warning("Connection is not available, retrying...");

                    if (futureSocket.isPresent()) {

                        socket = futureSocket.get();

                        dataIn = new ObjectInputStream(socket.getInputStream());
                        dataOut = new ObjectOutputStream(socket.getOutputStream());

                        connected = true;
                        pluginInstance.log("Connected to the proxy!");
                    }

                }

            } catch (IOException | ClassNotFoundException | InterruptedException | ExecutionException | TimeoutException e){
                pluginInstance.error("There was an error while handling data from the server!");

                connected = false;

                e.printStackTrace();
            }


        } while (running);

    }


    private Future<Optional<Socket>> connect() {
        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
    }

    // TODO: This is stupid and very unsafe. Fix it sometime, whore.
    public void shutdown() {
        running = false;
    }

    public Optional<Packet> read() throws IOException, ClassNotFoundException {
        Object objectIn = dataIn.readObject();

        if (objectIn instanceof Packet) {

            Packet packetIn = (Packet) objectIn;
            return Optional.of(packetIn);

        } else {

            pluginInstance.warning("Packet received from " + serverAddress + ", but does not appear to be valid. Ignoring it.");
        }


        return Optional.empty();

    }

    public void send_direct(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "...");
        try {
            dataOut.writeObject(packetIn);
            dataOut.flush();

        } catch (IOException e) {
            if (connected) {
                pluginInstance.error("That packed failed to send :(");
                e.printStackTrace();
            }
        }
    }

    public Optional<Packet> send(Packet packetIn) {

        try {

            send_direct(packetIn);

            Supplier<Optional<Packet>> getPacket = () -> {
                if (connected) {
                    try {
                        return read();
                    } catch (IOException | ClassNotFoundException e) {
                        pluginInstance.error("Trying to receive a packet failed:");
                        e.printStackTrace();
                    }
                }
                return Optional.empty();
            };

            return CompletableFuture.supplyAsync(getPacket).get(5, TimeUnit.SECONDS);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            pluginInstance.error("Trying to send a packet failed.");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean isRunning() {
        return running;
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

    // this is a singleton. yes, i am aware other classes are like this
    public static ClientConnection getClientConnection() {
        return clientConnection;
    }
}
