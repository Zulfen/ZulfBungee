package tk.zulfengaming.zulfbungee.spigot.socket;

import org.bukkit.ChatColor;
import tk.zulfengaming.zulfbungee.spigot.handlers.transport.ClientSocketCommHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.connections.SocketConnectionManager;

import java.io.IOException;
import java.net.Socket;

public class SocketConnection extends Connection {

    private final SocketConnectionManager socketConnectionManager;

    public SocketConnection(SocketConnectionManager connectionManagerIn, Socket socketIn) throws IOException {
        super(connectionManagerIn.getPluginInstance(), socketIn.getRemoteSocketAddress());
        setClientCommHandler(new ClientSocketCommHandler(pluginInstance, socketIn));
        this.socketConnectionManager = connectionManagerIn;
        socketConnectionManager.register();
    }

    @Override
    public void run() {
        pluginInstance.logInfo(String.format("%sConnection established with proxy! (%s)", ChatColor.GREEN, socketAddress));
        super.run();
    }

    public void destroy() {
        socketConnectionManager.deRegister();
        super.destroy();
    }

}