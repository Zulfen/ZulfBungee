package tk.zulfengaming.zulfbungee.bungeecord.socket;

import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseServerConnection implements Runnable {

    private final Server server;
    // plugin instance ?
    private final ZulfBungeecord pluginInstance;

    private final Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(true);

    private final SocketAddress address;

    // handling packets
    private final PacketHandlerManager packetManager;

    // data I/O
    private final DataInHandler dataInHandler;
    private final DataOutHandler dataOutHandler;

    private ServerInfo serverInfo;

    private Packet packetInBuffer;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public BaseServerConnection(Server serverIn, Socket socketIn) throws IOException {
        this.socket = socketIn;

        this.packetManager = serverIn.getPacketManager();

        this.pluginInstance = serverIn.getPluginInstance();
        this.server = serverIn;

        this.address = socket.getRemoteSocketAddress();

        this.dataInHandler = new DataInHandler(this);
        this.dataOutHandler = new DataOutHandler(this);

        pluginInstance.getTaskManager().newTask(dataInHandler, "DataInHandler");
        pluginInstance.getTaskManager().newTask(dataOutHandler, "DataOutHandler");
    }


    public void run() {

        do {

            try {

                if (socketConnected.get()) {

                    Packet packetIn = dataInHandler.getQueue().poll(5, TimeUnit.SECONDS);
                    packetInBuffer = packetIn;

                    if (packetIn != null) {
                        Packet handledPacket = packetManager.handlePacket(packetIn, this);

                        if (packetIn.isReturnable() && handledPacket != null) {
                            send(handledPacket);
                        }
                    }
                }

            } catch (InterruptedException ignored) {

            }

        } while (running.get());


    }

    public void shutdown() {
        running.compareAndSet(true, false);
    }

    public void end()  {

        if (running.compareAndSet(true, false)) {

            pluginInstance.logInfo("Disconnecting client " + address);

            server.removeServerConnection(this);

            try {

                socket.close();

            } catch (IOException e) {

                pluginInstance.error("Error closing socket on connection " + address);

                e.printStackTrace();
            }
        }
    }

    private Optional<Packet> read() {
        return Optional.ofNullable(packetInBuffer);

    }

    public void send(Packet packetIn) {

        try {

            dataOutHandler.getQueue().put(packetIn);

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed to send.");
            e.printStackTrace();
        }

        if (packetIn.getType() != PacketTypes.HEARTBEAT) {
            pluginInstance.logDebug("Sent packet " + packetIn.getType().toString() + "...");
        }

    }

    public Server getServer() {
        return server;
    }

    public ServerInfo getClientInfo() {
        return serverInfo;
    }

    public void setClientInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Socket getSocket() {
        return socket;
    }

    public ZulfBungeecord getPluginInstance() {
        return pluginInstance;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public AtomicBoolean isSocketConnected() {
        return socketConnected;
    }

    public AtomicBoolean isRunning() {
        return running;
    }
}
