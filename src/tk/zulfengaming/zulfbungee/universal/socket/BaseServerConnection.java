package tk.zulfengaming.zulfbungee.universal.socket;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.universal.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.universal.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseServerConnection implements Runnable {

    private final MainServer mainServer;
    // plugin instance ?
    private final ZulfBungeeProxy pluginInstance;

    private final Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(true);
    private long ping = 0;

    private final SocketAddress address;

    // handling packets
    private final PacketHandlerManager packetManager;

    // data I/O
    private final DataInHandler dataInHandler;
    private final DataOutHandler dataOutHandler;

    private final TransferQueue<Packet> readQueue = new LinkedTransferQueue<>();

    private ServerInfo serverInfo;

    // initially empty
    private String name = "";

    private final AtomicBoolean running = new AtomicBoolean(true);

    public BaseServerConnection(MainServer mainServerIn, Socket socketIn) throws IOException {

        this.socket = socketIn;

        this.packetManager = mainServerIn.getPacketManager();

        this.pluginInstance = mainServerIn.getPluginInstance();

        this.mainServer = mainServerIn;

        this.address = socket.getRemoteSocketAddress();

        this.dataInHandler = new DataInHandler(this);
        this.dataOutHandler = new DataOutHandler(this);

        pluginInstance.getTaskManager().newTask(dataInHandler);
        pluginInstance.getTaskManager().newTask(dataOutHandler);

    }


    public void run() {

        do {

            try {

                if (socketConnected.get()) {

                    Packet packetIn = dataInHandler.getQueue().poll(5, TimeUnit.SECONDS);

                    if (packetIn != null) {

                        if (packetIn.shouldHandle() && readQueue.hasWaitingConsumer()) {
                            readQueue.tryTransfer(packetIn);
                        }

                        try {

                            Packet handledPacket = packetManager.handlePacket(packetIn, this);

                            if (packetIn.isReturnable() && handledPacket != null) {
                                sendDirect(handledPacket);
                            }

                        } catch (Exception e) {

                            // Used if unhandled exception occurs
                            pluginInstance.error(String.format("Unhandled exception occurred in connection with address %s", socket.getRemoteSocketAddress()));
                            e.printStackTrace();

                            end();

                        }

                    }
                }

            } catch (InterruptedException e) {
                break;
            }

        } while (running.get());


    }

    public void shutdown() {
        running.compareAndSet(true, false);
    }

    public void end()  {

        if (running.compareAndSet(true, false)) {

            mainServer.removeServerConnection(this);

            try {

                socket.close();

            } catch (IOException e) {

                pluginInstance.error("Error closing socket on connection " + address);

                e.printStackTrace();
            }
        }
    }

    // Customisable poll time, will just impl it this way for now!
    /*private Optional<Packet> read(long lengthIn, TimeUnit timeUnitIn) {
        try {
            return Optional.ofNullable(readQueue.poll(lengthIn, timeUnitIn));
        } catch (InterruptedException e) {
            return Optional.empty();
        }

    }

    public Optional<Packet> send(Packet packetIn, long lengthIn, TimeUnit timeUnitIn) {
        sendDirect(packetIn);
        return read(lengthIn, timeUnitIn);
    }*/

    public void sendDirect(Packet packetIn) {

        try {

            dataOutHandler.getQueue().put(packetIn);

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed to send due to thread interruption?:");
            pluginInstance.error(packetIn.toString());
        }

        if (packetIn.getType() != PacketTypes.HEARTBEAT) {
            pluginInstance.logDebug("Sent packet " + packetIn.getType().toString() + "...");
        }

    }

    // input null into senderIn to make the console reload the scripts, not a player.
    public void sendScript(Path scriptPathIn, ScriptAction actionIn, ProxyCommandSender senderIn) {

        pluginInstance.getTaskManager().newTask(() -> {

            String scriptName = scriptPathIn.getFileName().toString();

            ProxyPlayer playerOut = null;

            if (senderIn != null) {
                if (senderIn.isPlayer()) {
                    ProxiedPlayer playerIn = (ProxiedPlayer) senderIn;
                    playerOut = new ProxyPlayer(playerIn.getName(), playerIn.getUniqueId());
                }
            }

            try {

                byte[] data = Files.readAllBytes(scriptPathIn);

                sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, false, true, new ScriptInfo(actionIn,
                        scriptName, playerOut, data)));

            } catch (IOException e) {
                pluginInstance.error(String.format("Error while parsing script %s!", scriptName));
                e.printStackTrace();
            }

        });

    }

    public MainServer getServer() {
        return mainServer;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZulfBungeeProxy getPluginInstance() {
        return pluginInstance;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public long getPing() {
        return ping;
    }

    public void setPing(long ping) {
        this.ping = ping;
    }

    public AtomicBoolean isSocketConnected() {
        return socketConnected;
    }

    public AtomicBoolean isRunning() {
        return running;
    }
}
