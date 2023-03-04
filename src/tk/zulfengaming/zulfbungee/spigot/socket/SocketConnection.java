package tk.zulfengaming.zulfbungee.spigot.socket;

import net.md_5.bungee.api.ChatColor;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
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
        this.dataOutHandler = new DataOutHandler(this);
        this.dataInHandler = new DataInHandler(this);

    }

    public void run() {

        Thread.currentThread().setName("ClientConnection");

        taskManager.newAsyncTask(dataInHandler);
        taskManager.newAsyncTask(dataOutHandler);

        pluginInstance.logInfo(org.bukkit.ChatColor.GREEN + "Connection established with proxy!");

        ClientInfo zulfServerInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());

        sendDirect(new Packet(PacketTypes.PROXY_CLIENT_INFO, true, true, zulfServerInfo));
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

                    }

                } else {
                    skriptPacketQueue.offer(Optional.empty());
                    shutdown();
                }

            } catch (InterruptedException e) {
                break;
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

    public int getTimeout() {
        return 2000;
    }

    public void end() {

        if (running.compareAndSet(true, false)) {

            socketConnected.compareAndSet(true, false);

            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dataInHandler.shutdown();
            dataOutHandler.shutdown();

            dataInHandler.cancel();
            dataOutHandler.cancel();

        }

    }

    public InputStream getInputStream() {

        try {
            if (socketConnected.get()) {
                return socket.getInputStream();
            } else {
                throw new RuntimeException("Socket is not connected!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public OutputStream getOutputStream() {

        try {
            if (socketConnected.get()) {
                return socket.getOutputStream();
            } else {
                throw new RuntimeException("Socket is not connected!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
