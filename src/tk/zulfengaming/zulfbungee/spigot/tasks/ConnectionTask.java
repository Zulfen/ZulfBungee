package tk.zulfengaming.zulfbungee.spigot.tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.SocketHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionTask extends BukkitRunnable {

    private final ConnectionManager connectionManager;
    private final ZulfBungeeSpigot pluginInstance;

    private final TaskManager taskManager;

    private final SocketHandler socketHandler;

    private final CyclicBarrier connectionBarrier;

    public ConnectionTask(ConnectionManager connectionManagerIn, CyclicBarrier connectionBarrierIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort, int timeOut) {
        this.connectionManager = connectionManagerIn;
        this.socketHandler = new SocketHandler(clientAddress, clientPort, serverAddress, serverPort, timeOut);
        this.pluginInstance = connectionManager.getPluginInstance();
        this.taskManager = pluginInstance.getTaskManager();
        this.connectionBarrier = connectionBarrierIn;
    }

    @Override
    public void run() {

        do {

            try {

                while (connectionManager.getRegistered() < 1) {

                    Optional<Socket> newSocket = taskManager.submitCallable(socketHandler);

                    if (newSocket.isPresent()) {

                        SocketConnection mainConnection = new SocketConnection(connectionManager, newSocket.get());
                        connectionManager.addInactiveConnection(mainConnection);
                        taskManager.newAsyncTask(mainConnection);

                    }

                }

                connectionBarrier.await();

            } catch (IOException e) {
                pluginInstance.warning("Connection lost with proxy, attempting to connect every 2 seconds...");
            } catch (InterruptedException e) {
                break;
            } catch (RejectedExecutionException ignored) {
                // ignored as we specifically throw this exception upon shutting down, we don't need to do any more work
            } catch (ExecutionException e) {
                pluginInstance.logDebug(ChatColor.RED + String.format("Error while creating socket: %s", e.getCause().getMessage()));
            } catch (BrokenBarrierException e) {
                // barrier only reset on shutdown, can be ignored
            }

        } while (connectionManager.isRunning().get());


    }
}
