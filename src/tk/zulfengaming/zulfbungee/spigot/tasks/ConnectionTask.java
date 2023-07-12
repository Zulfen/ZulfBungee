package tk.zulfengaming.zulfbungee.spigot.tasks;

import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;

import javax.net.SocketFactory;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ConnectionTask implements Runnable {

    private final ConnectionManager connectionManager;
    private final ZulfBungeeSpigot pluginInstance;

    private final TaskManager taskManager;

    private final Semaphore connectionBarrier;

    private InetAddress clientAddress;
    private int clientPort;

    private final InetAddress serverAddress;
    private final int serverPort;

    public ConnectionTask(ConnectionManager connectionManagerIn, Semaphore connectionBarrier, InetAddress serverAddress, int serverPort) {
        this.connectionManager = connectionManagerIn;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.pluginInstance = connectionManager.getPluginInstance();
        this.taskManager = pluginInstance.getTaskManager();
        this.connectionBarrier = connectionBarrier;
    }

    public ConnectionTask(ConnectionManager connectionManagerIn, Semaphore connectionBarrier, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort) {

        this.connectionManager = connectionManagerIn;

        this.clientAddress = clientAddress;
        this.clientPort = clientPort;

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        this.pluginInstance = connectionManager.getPluginInstance();
        this.taskManager = pluginInstance.getTaskManager();
        this.connectionBarrier = connectionBarrier;

    }

    @Override
    public void run() {

        Thread.currentThread().setName("ConnectionTask");

        int finished = 0;
        boolean chooseRandomPort = pluginInstance.getConfig().getBoolean("choose-random-port");

        if (chooseRandomPort) {
            pluginInstance.logInfo("Connecting using a random port...");
        }

        pluginInstance.warning("Reconnecting every 2 seconds...");

        while (finished < 1 && connectionManager.isRunning().get()) {

            try {

                SocketAddress proxyAddress = new InetSocketAddress(serverAddress, serverPort);
                String transportType = pluginInstance.getTransportType();

                if (!connectionManager.isBlocked(proxyAddress)) {
                    if (transportType.equalsIgnoreCase("socket")) {

                        Socket socket;

                        if (chooseRandomPort) {
                            socket = SocketFactory.getDefault().createSocket(serverAddress, serverPort);
                        } else {
                            socket = SocketFactory.getDefault().createSocket(serverAddress, serverPort, clientAddress, clientPort);
                        }

                        connectionManager.newSocketConnection(socket);


                    } else if (transportType.equalsIgnoreCase("pluginmessage")) {
                        // passes in the IP of the proxy which is only used for the sake of the existing system
                        // which requires one.
                        connectionManager.newChannelConnection(proxyAddress);
                    } else {
                        throw new RuntimeException("Invalid transport type! Please refer to the config.");
                    }

                    finished++;

                    connectionBarrier.release();

                }

            } catch (EOFException | SocketException e) {

                if (pluginInstance.isDebug()) {

                    pluginInstance.warning("Could not connect to the proxy!:");

                    StackTraceElement[] stackTrace = e.getStackTrace();

                    for (int i = 0; i < Math.min(6, stackTrace.length); i++) {
                        pluginInstance.warning(stackTrace[i].toString());
                    }

                    if (e instanceof EOFException) {
                        pluginInstance.warning("This exception shouldn't normally occur - ideally, report this to the developers!");
                    }

                }


            } catch (IOException ex) {

                if (pluginInstance.isDebug()) {
                    ex.printStackTrace();
                }

            } finally {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }

    }


}
