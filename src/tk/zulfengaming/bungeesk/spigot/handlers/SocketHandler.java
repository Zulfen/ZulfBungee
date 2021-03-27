package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientManager;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;

public class SocketHandler extends ClientListener implements Callable<Optional<Socket>> {

    private final ClientManager clientManager;

    public SocketHandler(ClientManager clientManagerIn) {
        super(clientManagerIn);

        this.clientManager = clientManagerIn;


    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onShutdown() {
        Thread.currentThread().interrupt();
    }

    @Override
    public Optional<Socket> call() throws InterruptedException {

        try {
            Socket socket = new Socket(getServerAddress(), getServerPort(), getClientAddress(), getClientPort());
            socket.setReuseAddress(true);

            return Optional.of(socket);

        } catch (IOException e) {
            Thread.sleep(2000);
        }

        return Optional.empty();

    }
}

