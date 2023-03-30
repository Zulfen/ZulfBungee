package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.universal.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseServerConnection<P> implements Runnable {

    private final MainServer<P> mainServer;

    // plugin instance ?
    protected final ZulfBungeeProxy<P> pluginInstance;

    private final Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(true);

    private final SocketAddress address;

    // handling packets
    private final PacketHandlerManager<P> packetManager;

    // data I/O
    private final DataInHandler<P> dataInHandler;
    private final DataOutHandler<P> dataOutHandler;

    private final TransferQueue<Packet> readQueue = new LinkedTransferQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);

    public BaseServerConnection(MainServer<P> mainServerIn, Socket socketIn) throws IOException {

        this.socket = socketIn;

        this.packetManager = mainServerIn.getPacketManager();

        this.pluginInstance = mainServerIn.getPluginInstance();

        this.mainServer = mainServerIn;

        this.address = socket.getRemoteSocketAddress();

        try {
            this.dataInHandler = new DataInHandler<>(this, socketIn);
            this.dataOutHandler = new DataOutHandler<>(this, socketIn);
        } catch (IOException e) {
            throw new IOException("Could not establish a connection properly!");
        }


    }


    public void run() {

        pluginInstance.getTaskManager().newTask(dataInHandler);
        pluginInstance.getTaskManager().newTask(dataOutHandler);


        do {

            try {

                if (socketConnected.get()) {

                    Optional<Packet> packetIn = dataInHandler.getQueue().take();

                    if (packetIn.isPresent()) {

                        Packet packet = packetIn.get();

                        if (packet.shouldHandle() && readQueue.hasWaitingConsumer()) {
                            readQueue.tryTransfer(packet);
                        }

                        processPacket(packet);

                    }



                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } while (running.get());


    }

    protected void processPacket(Packet packetIn) {

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

    public void end()  {

        if (running.compareAndSet(true, false)) {

            socketConnected.compareAndSet(true, false);

            mainServer.removeServerConnection(this);

            if (dataInHandler != null && dataOutHandler != null) {
                dataInHandler.shutdown();
                dataOutHandler.shutdown();
            }

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

            dataOutHandler.getQueue().put(Optional.of(packetIn));

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed to send due to thread interruption?:");
            pluginInstance.error(packetIn.toString());
        }

        if (packetIn.getType() != PacketTypes.HEARTBEAT) {
            pluginInstance.logDebug("Sent packet " + packetIn + "...");
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

    public abstract List<ZulfProxyPlayer<P>> getPlayers();

    public ZulfBungeeProxy<P> getPluginInstance() {
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
