package tk.zulfengaming.zulfbungee.spigot.tasks;

import org.bukkit.ChatColor;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.SocketHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

public class ConnectionTask implements Runnable {

    private final ConnectionManager connectionManager;
    private final ZulfBungeeSpigot pluginInstance;

    private final TaskManager taskManager;

    private final SocketHandler socketHandler;
    private final Semaphore connectionBarrier;

    public ConnectionTask(ConnectionManager connectionManagerIn, Semaphore connectionBarrier, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort, int timeOut) {
        this.connectionManager = connectionManagerIn;
        this.socketHandler = new SocketHandler(clientAddress, clientPort, serverAddress, serverPort, timeOut);
        this.pluginInstance = connectionManager.getPluginInstance();
        this.taskManager = pluginInstance.getTaskManager();
        this.connectionBarrier = connectionBarrier;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("ConnectionTask");

        try {

            if (connectionManager.getRegistered() < 1) {

                Optional<Socket> newSocket = taskManager.submitCallable(socketHandler);

                if (newSocket.isPresent()) {

                    SocketConnection mainConnection = new SocketConnection(connectionManager, newSocket.get());
                    connectionManager.addInactiveConnection(mainConnection);
                    taskManager.newAsyncTask(mainConnection);

                }

            }

        } catch (IOException e) {
            pluginInstance.warning("Connection lost with proxy, attempting to connect every 2 seconds...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (RejectedExecutionException ignored) {
            // ignored as we specifically throw this exception upon shutting down, we don't need to do any more work
        } catch (ExecutionException e) {
            pluginInstance.logDebug(ChatColor.RED + String.format("Error while creating socket: %s", e.getCause().getMessage()));
        } finally {
            connectionBarrier.release();
        }


    }
}
