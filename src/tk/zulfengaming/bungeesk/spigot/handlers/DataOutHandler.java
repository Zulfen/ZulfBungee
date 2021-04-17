package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataOutHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final ClientListenerManager clientListenerManager;

    private final BlockingQueue<Packet> queueOut = new ArrayBlockingQueue<>(10);

    private ObjectOutputStream outputStream;

    public DataOutHandler(ClientListenerManager clientListenerManagerIn, ClientConnection connectionIn) {
        super(clientListenerManagerIn);

        this.connection = connectionIn;
        this.clientListenerManager = clientListenerManagerIn;

    }


    @Override
    public void run() {
        do {
            try {

                if (clientListenerManager.isSocketConnected()) {

                    Packet packetOut = queueOut.take();

                    outputStream.writeObject(packetOut);
                    outputStream.flush();

                } else {

                    synchronized (clientListenerManager.getSocketLock()) {
                        clientListenerManager.getSocketLock().wait();
                    }

                    outputStream = new ObjectOutputStream(clientListenerManager.getSocket().getOutputStream());

                }

            } catch (IOException e) {
                clientListenerManager.setSocketConnected(false);

            } catch (InterruptedException ignored) {

            }

        } while (connection.isRunning());
    }

    @Override
    public void onDisconnect() {

        try {

            if (connection.isConnected()) {
                outputStream.close();
            }

        } catch (IOException e) {

            clientListenerManager.getPluginInstance().error("Error closing input stream:");

            e.printStackTrace();
        }

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public void onConnect() {

    }

    public BlockingQueue<Packet> getQueue() {
        return queueOut;
    }
}
