package tk.zulfengaming.zulfbungee.spigot.handlers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SocketHandler implements Callable<Optional<Socket>> {

    private final int timeout;
    private final int clientPort;
    private final int serverPort;

    private final InetAddress clientAddress;
    private final InetAddress serverAddress;

    public SocketHandler(InetAddress clientAddressIn, int clientPortIn, InetAddress serverAddressIn, int serverPortIn, int timeoutIn) {
        this.timeout = timeoutIn;
        this.clientPort = clientPortIn;
        this.serverPort = serverPortIn;
        this.clientAddress = clientAddressIn;
        this.serverAddress = serverAddressIn;
    }


    @Override
    public Optional<Socket> call() throws InterruptedException {

        Socket socket = new Socket();

        try {

            socket.setReuseAddress(true);

            socket.bind(new InetSocketAddress(clientAddress, clientPort));
            socket.connect(new InetSocketAddress(serverAddress, serverPort), timeout);

        } catch (IOException e) {
            TimeUnit.SECONDS.sleep(2);
            throw new RuntimeException(e);
        }

        return socket.isConnected() ? Optional.of(socket) : Optional.empty();

    }
}