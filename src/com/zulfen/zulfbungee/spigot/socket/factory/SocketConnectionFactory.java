package com.zulfen.zulfbungee.spigot.socket.factory;

import com.zulfen.zulfbungee.spigot.interfaces.ConnectionFactory;
import com.zulfen.zulfbungee.spigot.managers.connections.SocketConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.SocketClientConnection;

import java.io.IOException;
import java.net.Socket;

public class SocketConnectionFactory extends ConnectionFactory<SocketClientConnection, SocketConnectionManager> {

    private Socket socket;

    public SocketConnectionFactory(SocketConnectionManager connectionManagerIn) {
        super(connectionManagerIn);
    }

    public SocketConnectionFactory withSocket(Socket socketIn) {
        socket = socketIn;
        return this;
    }

    @Override
    public SocketClientConnection build() throws IOException {
        SocketClientConnection clientSocketConnection = new SocketClientConnection(connectionManager, socket);
        connectionManager.registerBefore();
        return clientSocketConnection;
    }

}
