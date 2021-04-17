package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataInHandler extends ClientListener implements Runnable {

    private final ClientConnection connection;

    private final ClientListenerManager clientListenerManager;

    private final BlockingQueue<Packet> queueIn = new ArrayBlockingQueue<>(10);

    private ObjectInputStream inputStream;


    public DataInHandler(ClientListenerManager clientListenerManagerIn, ClientConnection connectionIn) {
        super(clientListenerManagerIn);

        this.clientListenerManager = clientListenerManagerIn;

        this.connection = connectionIn;

    }


    @Override
    public void run() {
        do {
            try {

                if (clientListenerManager.isSocketConnected()) {

                    Object dataIn = inputStream.readObject();

                    if (dataIn instanceof Packet) {

                        queueIn.put((Packet) dataIn);

                    }

                } else {

                    synchronized (clientListenerManager.getSocketLock()) {
                        clientListenerManager.getSocketLock().wait();
                    }

                    inputStream = new ObjectInputStream(clientListenerManager.getSocket().getInputStream());


                }

            } catch (IOException e) {
                clientListenerManager.setSocketConnected(false);

            } catch (ClassNotFoundException e) {
                clientListenerManager.getPluginInstance().error("There was an error running the server! Disconnecting");

                e.printStackTrace();


            } catch (InterruptedException ignored) {

            }

        } while (connection.isRunning());
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public void onConnect() {

    }

    public BlockingQueue<Packet> getQueue() {
        return queueIn;
    }

}
