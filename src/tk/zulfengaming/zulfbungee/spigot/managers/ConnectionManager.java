package tk.zulfengaming.zulfbungee.spigot.managers;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.SocketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionManager extends BukkitRunnable {

    private final ZulfBungeeSpigot pluginInstance;

    private final HashMap<String, ClientConnection> connections = new HashMap<>();
    private final HashMap<String, ClientInfo> proxyServers = new HashMap<>();

    private final SocketHandler socketHandler;

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final Phaser socketBarrier = new Phaser();

    public ConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort, int timeOut) {
        this.pluginInstance = pluginIn;
        this.socketHandler = new SocketHandler(clientAddress, clientPort, serverAddress, serverPort, timeOut);
        socketBarrier.register();
    }

    @Override
    public void run() {

        do {


            socketBarrier.arriveAndAwaitAdvance();


        } while(running.get());

    }
}
