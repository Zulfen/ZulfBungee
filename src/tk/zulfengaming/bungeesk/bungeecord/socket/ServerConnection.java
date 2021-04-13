package tk.zulfengaming.bungeesk.bungeecord.socket;

import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.handlers.DataInHandler;
import tk.zulfengaming.bungeesk.bungeecord.handlers.DataOutHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.*;

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

    private final BlockingQueue<Packet> packetInBuffer = new SynchronousQueue<>();

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
        this.dataOutHandler = new DataOutHandler(this);

    }

    public void run() {

        do {

            try {

                pluginInstance.log("Waiting for packet");

                Packet packetIn = dataInHandler.getQueue().take();

                packetInBuffer.add(packetIn);

                dataOutHandler.getQueue().put(packetManager.handlePacket(packetIn, address));

            } catch (InterruptedException e) {
                end();

            }

        } while (running && socket.isConnected());

        end();


    }

    public void end()  {
        pluginInstance.log("Disconnecting client " + address + " (" + id + ")");

        dataOutHandler.disconnect();
        dataInHandler.disconnect();

        running = false;

        try {

            socket.close();

        } catch (IOException e) {
            pluginInstance.error("Error closing data streams in a server connection:");

            e.printStackTrace();
        }

        server.removeConnection(this);

    }

    private Optional<Packet> read() throws InterruptedException {
        return Optional.ofNullable(packetInBuffer.poll(5, TimeUnit.SECONDS));

    }

    public void send(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "...");

        try {

            dataOutHandler.getQueue().put(packetIn);

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed ");
            e.printStackTrace();
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
