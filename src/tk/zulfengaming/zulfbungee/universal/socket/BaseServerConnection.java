package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.universal.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.*;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseServerConnection<P> implements Runnable {

    private final MainServer<P> mainServer;

    // plugin instance ?
    protected final ZulfBungeeProxy<P> pluginInstance;

    private final Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(true);
    private long ping = 0;

    private final SocketAddress address;

    // handling packets
    private final PacketHandlerManager<P> packetManager;

    // data I/O
    private final DataInHandler<P> dataInHandler;
    private final DataOutHandler<P> dataOutHandler;

    private final TransferQueue<Packet> readQueue = new LinkedTransferQueue<>();

    private ClientInfo clientInfo;

    // initially empty
    protected String name = "";

    private final AtomicBoolean running = new AtomicBoolean(true);

    public BaseServerConnection(MainServer<P> mainServerIn, Socket socketIn) throws IOException {

        this.socket = socketIn;

        this.packetManager = mainServerIn.getPacketManager();

        this.pluginInstance = mainServerIn.getPluginInstance();

        this.mainServer = mainServerIn;

        this.address = socket.getRemoteSocketAddress();

        this.dataInHandler = new DataInHandler<>(this);
        this.dataOutHandler = new DataOutHandler<>(this);

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
    public void sendScript(Path scriptPathIn, ScriptAction actionIn, ProxyCommandSender<P> senderIn) {

        pluginInstance.getTaskManager().newTask(() -> {

            String scriptName = scriptPathIn.getFileName().toString();

            ClientPlayer playerOut = null;

            if (senderIn != null) {
                if (senderIn.isPlayer()) {
                    ZulfProxyPlayer<P> playerIn = (ZulfProxyPlayer<P>) senderIn;
                    playerOut = new ClientPlayer(playerIn.getName(), playerIn.getUuid());
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

    public MainServer<P> getServer() {
        return mainServer;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
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

    public abstract List<ZulfProxyPlayer<P>> getPlayers();

    public ZulfBungeeProxy<P> getPluginInstance() {
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
