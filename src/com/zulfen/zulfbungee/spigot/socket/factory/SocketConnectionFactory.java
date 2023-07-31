package com.zulfen.zulfbungee.spigot.socket.factory;

import com.zulfen.zulfbungee.spigot.interfaces.ConnectionFactory;
import com.zulfen.zulfbungee.spigot.managers.connections.SocketConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.ClientSocketConnection;

import java.io.IOException;
import java.net.Socket;

public class SocketConnectionFactory extends ConnectionFactory<ClientSocketConnection, SocketConnectionManager> {

    private Socket socket;

    public SocketConnectionFactory(SocketConnectionManager connectionManagerIn) {
        super(connectionManagerIn);
    }

    public SocketConnectionFactory withSocket(Socket socketIn) {
        socket = socketIn;
        return this;
    }

    @Override
    public ClientSocketConnection build() {
        try {
            ClientSocketConnection clientSocketConnection = new ClientSocketConnection(connectionManager, socket);
            connectionManager.registerBefore();
            return clientSocketConnection;
        } catch (IOException e) {
            throw new RuntimeException("Error creating new socket connection!", e);
        }
    }

}
