package tk.zulfengaming.zulfbungee.spigot.socket;

import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketConnection extends Connection {

    private final Socket socket;

    private final AtomicBoolean socketConnected = new AtomicBoolean(true);

    private final DataOutHandler dataOutHandler;

    private final DataInHandler dataInHandler;

    private final TaskManager taskManager;

    public SocketConnection(ConnectionManager connectionManagerIn, Socket socketIn) throws IOException {

        super(connectionManagerIn);

        this.taskManager = pluginInstance.getTaskManager();
        this.socket = socketIn;

        try {
            this.dataOutHandler = new DataOutHandler(this, socketIn);
            this.dataInHandler = new DataInHandler(this, socketIn);
        } catch (IOException e) {
            shutdown();
            throw new IOException("Could not establish a connection properly!");
        }


    }

    public void run() {

        taskManager.newAsyncTask(dataInHandler);
        taskManager.newAsyncTask(dataOutHandler);

        Thread.currentThread().setName(String.format("ClientConnection@%s", socket.getRemoteSocketAddress()));

        pluginInstance.logInfo(org.bukkit.ChatColor.GREEN + "Connection established with proxy!");

        String forcedName = pluginInstance.getConfig().getString("forced-connection-name");

        if (forcedName.isEmpty()) {
            sendDirect(new Packet(PacketTypes.PROXY_CLIENT_INFO, true, true, clientInfo));
        } else {
            sendDirect(new Packet(PacketTypes.CONNECTION_NAME, true, true, new Object[]{forcedName, clientInfo}));
        }

        sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, true, true, new Object[0]));

        do {
            try {

                if (socketConnected.get()) {

                    Optional<Packet> packetIn = dataInHandler.getDataQueue().take();

                    if (packetIn.isPresent()) {

                        Packet packet = packetIn.get();

                        if (packet.shouldHandle()) {
                            packetHandlerManager.handlePacket(packet, socket.getRemoteSocketAddress());
                        } else {
                            skriptPacketQueue.put(packetIn);
                        }

                    } else {
                        skriptPacketQueue.put(Optional.empty());
                    }

                    pluginInstance.logDebug(String.format("Received packet %s", packetIn));


                } else {
                    skriptPacketQueue.put(Optional.empty());
                    shutdown();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (Exception e) {
                pluginInstance.error("An unhandled exception occurred in a connection! Please report this to the plugin developers:");
                pluginInstance.error("");
                e.printStackTrace();
                pluginInstance.error("");
                shutdown();
            }

        } while (running.get());

    }

    public void sendDirect(Packet packetIn) {

        try {

            if (socketConnected.get()) {

                dataOutHandler.getDataQueue().put(Optional.of(packetIn));

                if (packetIn.getType() != PacketTypes.HEARTBEAT) {
                    pluginInstance.logDebug("Sent packet " + packetIn + "...");
                }
            }

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed to send due to thread interruption?:");
            pluginInstance.error(packetIn.toString());
            Thread.currentThread().interrupt();
        }

    }


    @Override
    public SocketAddress getAddress() {
        return socket.getRemoteSocketAddress();
    }

    public AtomicBoolean isRunning() {
        return running;
    }

    public AtomicBoolean isConnected() {
        return socketConnected;
    }

    public void end() {

        if (running.compareAndSet(true, false)) {

            socketConnected.compareAndSet(true, false);
            skriptPacketQueue.offer(Optional.empty());

            if (dataInHandler != null && dataOutHandler != null) {

                dataInHandler.shutdown();
                dataOutHandler.shutdown();

                dataInHandler.cancel();
                dataOutHandler.cancel();

            }

            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }

}
