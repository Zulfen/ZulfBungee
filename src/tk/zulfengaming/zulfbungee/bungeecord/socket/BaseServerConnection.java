package tk.zulfengaming.zulfbungee.bungeecord.socket;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.bungeecord.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.*;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
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

        pluginInstance.getTaskManager().newTask(dataInHandler);
        pluginInstance.getTaskManager().newTask(dataOutHandler);

    }


    public void run() {

        do {

            try {

                if (socketConnected.get()) {

                    Packet packetIn = dataInHandler.getQueue().poll(5, TimeUnit.SECONDS);
                    packetInBuffer = packetIn;

                    if (packetIn != null) {

                        try {

                            Packet handledPacket = packetManager.handlePacket(packetIn, this);

                            if (packetIn.isReturnable() && handledPacket != null) {
                                send(handledPacket);
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
            pluginInstance.error("That packet failed to send due to thread interruption?:");
            pluginInstance.error(packetIn.toString());
        }

        if (packetIn.getType() != PacketTypes.HEARTBEAT) {
            pluginInstance.logDebug("Sent packet " + packetIn.getType().toString() + "...");
        }

    }

    // input null into senderIn to make the console reload the scripts, not a player.
    public void sendScript(Path scriptPathIn, ScriptAction actionIn, CommandSender senderIn) {

        pluginInstance.getTaskManager().newTask(() -> {

            String scriptName = scriptPathIn.getFileName().toString();

            ProxyPlayer playerOut = null;

            if (senderIn != null) {
                if (senderIn instanceof ProxiedPlayer) {
                    ProxiedPlayer playerIn = (ProxiedPlayer) senderIn;
                    playerOut = new ProxyPlayer(playerIn.getName(), playerIn.getUniqueId());
                }
            }

            try {

                byte[] data = Files.readAllBytes(scriptPathIn);

                send(new Packet(PacketTypes.GLOBAL_SCRIPT, false, true, new ScriptInfo(actionIn,
                        scriptName, playerOut, data)));

            } catch (IOException e) {
                pluginInstance.error(String.format("Error while parsing script %s!", scriptName));
                e.printStackTrace();
            }

        });

    }

    public Server getServer() {
        return server;
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
