package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.ChatColor;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class SocketHandler extends ClientListener implements Callable<Socket> {

    private final int timeout;

    public SocketHandler(ClientListenerManager clientListenerManagerIn) {
        super(clientListenerManagerIn);
        this.timeout = (int) Math.ceil((clientListenerManagerIn.getConnection().getHeartbeatTicks() / 20f) * 1000);
    }


    @Override
    public Socket call() throws IOException {

        InetAddress clientAddress = getClientListenerManager().getClientAddress();
        int clientPort = getClientListenerManager().getClientPort();

        InetAddress serverAddress = getClientListenerManager().getServerAddress();
        int serverPort = getClientListenerManager().getServerPort();

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(clientAddress, clientPort));
        socket.connect(new InetSocketAddress(serverAddress, serverPort), timeout);

        return socket;

    }
}