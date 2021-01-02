package tk.zulfengaming.bungeesk.bungeecord.socket;

import org.jetbrains.annotations.Nullable;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class ServerConnection implements Runnable {

    Server server;
    // plugin instance ?
    BungeeSkProxy pluginInstance;

    Socket socket;

    public SocketAddress address;
    public String name;

    // handling packets
    PacketHandlerManager packetManager;

    public boolean running = true;

    // direct access to IO
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;
    private Packet packetIn;

    public ServerConnection(Server serverIn) throws TaskAlreadyExists {
        this.socket = serverIn.socket;
        this.packetManager = serverIn.packetManager;
        this.pluginInstance = serverIn.pluginInstance;
        this.server = serverIn;

        this.address = socket.getRemoteSocketAddress();

    }

    public void run() {

        try {

            this.dataIn = new ObjectInputStream(socket.getInputStream());
            this.dataOut = new ObjectOutputStream(socket.getOutputStream());

            while (running && socket.isConnected()) {
                Packet packetIn = read();

                if (!(packetIn == null)) {
                    Packet processedPacket = packetManager.handlePacket(packetIn, address);

                    if (!(processedPacket == null) && packetIn.returnable) {
                        send(processedPacket);
                    }
                }
            }

            disconnect();

        } catch (StreamCorruptedException e) {
            pluginInstance.warning("The client at " + address + " sent some invalid data. Ignoring.");
        } catch (IOException | ClassNotFoundException e) {
            pluginInstance.error("There was an error while handling data for a connection!");
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        pluginInstance.log("Disconnecting client " + address);

        running = false;
        socket.close();
        dataIn.close();
        dataOut.close();

    }

    private @Nullable
    Packet convertPacket(Object object) {
        if (object instanceof Packet) {
            return (Packet) object;
        } else {
            pluginInstance.warning("Packet received from " + address + ", but does not appear to be valid. Ignoring it.");
            return null;
        }
    }

    public Packet read() throws IOException, ClassNotFoundException {
        return convertPacket(dataIn.readObject());
    }

    public Packet send(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.type.toString() + "...");

        try {
            dataOut.writeObject(packetIn);
            dataOut.flush();

            Supplier<Packet> response = () -> {
                try {
                    return read();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return null;
            };

            return CompletableFuture.supplyAsync(response).get(socket.getSoTimeout(), TimeUnit.MILLISECONDS);

        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            pluginInstance.error("That packed failed to send :(");
            e.printStackTrace();

        }
        return null;
    }
}
