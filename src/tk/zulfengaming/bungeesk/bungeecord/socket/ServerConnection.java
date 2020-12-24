package tk.zulfengaming.bungeesk.bungeecord.socket;

import org.jetbrains.annotations.Nullable;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerConnection implements Runnable {

    Server server;
    // plugin instance ?
    BungeeSkProxy instance;

    Socket socket;

    public SocketAddress address;
    public String name;

    // handling packets
    PacketHandlerManager packetManager;

    public boolean running = true;

    // direct access to IO
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;

    public ServerConnection(Server serverIn) throws TaskAlreadyExists {
        this.socket = serverIn.socket;
        this.packetManager = serverIn.packetManager;
        this.instance = serverIn.instance;
        this.server = serverIn;

        this.address = socket.getRemoteSocketAddress();


    }

    public void run() {
        try {
            this.dataIn = new ObjectInputStream(socket.getInputStream());
            this.dataOut = new ObjectOutputStream(socket.getOutputStream());

            while (running) {
                Packet packetIn = convertPacket(dataIn.readObject());

                if (!(packetIn == null)) {
                    Packet processedPacket = packetManager.handlePacket(packetIn, address);

                    if (!(processedPacket == null) && packetIn.returnable) {
                        send(processedPacket);
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            instance.log("There was an error while handling data for a connection!");
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        instance.log("Disconnecting client " + String.valueOf(address));

        running = false;

        dataIn.close();
        dataOut.close();

    }


    private @Nullable
    Packet convertPacket(Object object) {
        if (object instanceof Packet) {
            return (Packet) object;
        } else {
            instance.log("Packet received, but does not appear to be valid. Ignoring it.");
            return null;
        }
    }

    public Object read() throws IOException, ClassNotFoundException {
        return dataIn.readObject();
    }

    public void send(Packet packetIn) {
        instance.log("Sending packet " + packetIn.type.toString() + "...");
        try {
            dataOut.writeObject(packetIn);
            dataOut.flush();
        } catch (IOException e) {
            instance.log("That packed failed to send :(");
            e.printStackTrace();
        }
    }
}
