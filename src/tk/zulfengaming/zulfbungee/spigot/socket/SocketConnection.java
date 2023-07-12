package tk.zulfengaming.zulfbungee.spigot.socket;

import tk.zulfengaming.zulfbungee.spigot.handlers.ClientSocketCommHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;

import java.io.IOException;
import java.net.Socket;

public class SocketConnection extends Connection {

    public SocketConnection(ConnectionManager connectionManagerIn, Socket socketIn) throws IOException {
        super(connectionManagerIn, new ClientSocketCommHandler(connectionManagerIn.getPluginInstance(), socketIn), socketIn.getRemoteSocketAddress());
    }

}