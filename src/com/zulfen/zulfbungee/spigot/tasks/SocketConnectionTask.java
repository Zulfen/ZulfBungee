package com.zulfen.zulfbungee.spigot.tasks;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.connections.SocketConnectionManager;

import javax.net.SocketFactory;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SocketConnectionTask implements Runnable {

    private final SocketConnectionManager connectionManager;
    private final ZulfBungeeSpigot pluginInstance;

    private final Semaphore connectionBarrier;

    private final InetAddress clientAddress;
    private final int clientPort;

    private final InetAddress serverAddress;
    private final int serverPort;

    public SocketConnectionTask(SocketConnectionManager connectionManagerIn, Semaphore connectionBarrier, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort) {

        this.connectionManager = connectionManagerIn;

        this.clientAddress = clientAddress;
        this.clientPort = clientPort;

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        this.pluginInstance = connectionManager.getPluginInstance();
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
