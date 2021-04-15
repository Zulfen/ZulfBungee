package tk.zulfengaming.bungeesk.bungeecord.socket;

import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.handlers.DataInHandler;
import tk.zulfengaming.bungeesk.bungeecord.handlers.DataOutHandler;
import tk.zulfengaming.bungeesk.bungeecord.interfaces.PacketHandlerManager;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;

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

    private Packet packetInBuffer;

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

        pluginInstance.getTaskManager().newTask(dataInHandler, "DataInHandler");
        pluginInstance.getTaskManager().newTask(dataOutHandler, "DataOutHandler");

    }

    public void run() {

        do {

            try {

                pluginInstance.log("Waiting for packet");

                Packet packetIn = dataInHandler.getQueue().take();
                packetInBuffer = packetIn;

                Packet handledPacket = packetManager.handlePacket(packetIn, address);

                if (packetIn.isReturnable()) {
                    dataOutHandler.getQueue().put(handledPacket);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();

            }

        } while (running && socket.isConnected());


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

    private Optional<Packet> read() {
        return Optional.ofNullable(packetInBuffer);

    }

    public void send(Packet packetIn) {

        try {

            dataOutHandler.getQueue().put(packetIn);

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed ");
            e.printStackTrace();
        }

        if (!packetManager.getHandler(packetIn).shouldHideInDebug()) {
            pluginInstance.log("Sent packet " + packetIn.getType().toString() + "...");
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
