package tk.zulfengaming.bungeesk.bungeecord.socket;

import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.handlers.DataInHandler;
import tk.zulfengaming.bungeesk.spigot.handlers.DataOutHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerConnection implements Runnable {

    private final Server server;
    // plugin instance ?
    private final BungeeSkProxy pluginInstance;

    private final Socket socket;

    private final SocketAddress address;
    private final String id;

    // handling packets
    private final PacketHandlerManager packetManager;

    // data I/O
    private DataInHandler dataInHandler;
    private DataOutHandler dataOutHandler;

    private boolean running = true;

    public ServerConnection(Server serverIn, String idIn) throws IOException {
        this.socket = serverIn.getSocket();

        this.packetManager = serverIn.getPacketManager();

        this.pluginInstance = serverIn.getPluginInstance();
        this.server = serverIn;

        this.address = socket.getRemoteSocketAddress();
        this.id = idIn;

        init();
    }

    public void init() throws IOException {

        this.dataInHandler = new DataInHandler(this);

    }

    public void run() {

        try {

            while (running && socket.isConnected()) {
                Packet packetIn =

            }

            disconnect();

        } catch (StreamCorruptedException e) {
            pluginInstance.warning("The client at " + address + " sent some invalid data. Ignoring.");

        } catch (EOFException | TimeoutException | InterruptedException | ExecutionException e) {
            disconnect();

        } catch (IOException e) {
            pluginInstance.error("There was an error while handling data for a connection!");

            interrupted = true;
            disconnect();

            e.printStackTrace();

        }

    }

    public void disconnect()  {
        pluginInstance.log("Disconnecting client " + address + " (" + id + ")");

        running = false;

        try {

            socket.close();

        } catch (IOException e) {
            pluginInstance.error("Error closing data streams in a server connection:");

            e.printStackTrace();
        }

        server.removeConnection(this);

    }

    private Future<Optional<Packet>> read() {


    }

    public void send(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "...");

        pluginInstance.log("send is acquiring lock...");
        readWriteLock.writeLock().lock();
        pluginInstance.log("send unlocked");

        try {

            streamOut.writeObject(packetIn);
            streamOut.flush();

        } catch (IOException e) {
            pluginInstance.error("That packed failed to send :(");
            e.printStackTrace();

            disconnect();

        } finally {
            readWriteLock.writeLock().lock();
        }

    }

    public Server getServer() {
        return server;
    }

    public Socket getSocket() {
        return socket;
    }

    public BungeeSkProxy getPluginInstance() {
        return pluginInstance;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public boolean isRunning() {
        return running;
    }
}
