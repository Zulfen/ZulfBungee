package com.zulfen.zulfbungee.universal.managers.transport;

import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;
import com.zulfen.zulfbungee.universal.command.util.ChatColour;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.socket.transport.SocketServerConnection;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketMainServer<P, T> extends MainServer<P, T> implements Runnable {

    // setting up the server
    private final int port;
    private final InetAddress hostAddress;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean serverSocketAvailable = new AtomicBoolean(false);

    private ServerSocket serverSocket;
    private Socket socket;

    public SocketMainServer(int port, InetAddress address, ZulfBungeeProxy<P, T> instanceIn) {
        super(instanceIn);
        this.hostAddress = address;
        this.port = port;
    }

    public void run() {

        do {

            try {

                if (serverSocketAvailable.get()) {

                    socket = serverSocket.accept();
                    acceptSocketConnection(socket);


                } else {

                    try {

                        serverSocket = new ServerSocket(port, 50, hostAddress);

                    } catch (IOException e) {

                        pluginInstance.error("There was an error trying to start the server!");
                        pluginInstance.error("Please check your config to see if the port and host you specified is valid / not being used by another process.");
                        pluginInstance.error("Once you have done this, please restart this proxy server!");
                        pluginInstance.error("");
                        pluginInstance.error(e.toString());

                        break;
                    }

                    serverSocketAvailable.compareAndSet(false, true);

                    pluginInstance.logInfo(ChatColour.GREEN + "Waiting for connections on " + hostAddress + ":" + port);

                }

            } catch (SocketException | EOFException e) {

                if (pluginInstance.isDebug() && e instanceof EOFException) {
                    pluginInstance.warning("An uncommon error just occurred! This can be normal, but please report this to the developers!");
                    e.printStackTrace();
                }

                try {

                    if (socket != null) {
                        socket.close();
                    }

                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }


            } catch (IOException e) {

                if (pluginInstance.isDebug()) {
                    pluginInstance.warning("There was an error trying to establish a connection! Please consider restarting this proxy.");
                    e.printStackTrace();
                }

            }

        } while (running.get());
    }

    private void acceptSocketConnection(Socket socketIn) throws IOException {
        SocketServerConnection<P, T> connection = new SocketServerConnection<>(this, socketIn);
        startConnection(connection);
    }

    @Override
    public void end() throws IOException {
        if (running.compareAndSet(true, false)) {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        super.end();
    }

}
