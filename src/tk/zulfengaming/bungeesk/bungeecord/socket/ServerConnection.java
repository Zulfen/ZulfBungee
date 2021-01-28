package tk.zulfengaming.bungeesk.bungeecord.socket;

import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerConnection implements Runnable {

    Server server;
    // plugin instance ?
    BungeeSkProxy pluginInstance;

    Socket socket;

    public SocketAddress address;
    public String id;

    // handling packets
    PacketHandlerManager packetManager;

    public boolean running = true;

    // direct access to IO
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;

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

                if (dataIn.readObject() instanceof Packet) {

                    Packet packetIn = (Packet) dataIn.readObject();

                    Packet processedPacket = packetManager.handlePacket(packetIn, address);

                    if (!(processedPacket == null) && packetIn.returnable) {
                        send(processedPacket);

                    }
                } else {
                    pluginInstance.warning("Packet received from " + address + ", but does not appear to be valid. Ignoring it.");
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

        dataIn.close();
        dataOut.close();

        server.removeConnection(this);

    }

    public Packet read() throws IOException, ClassNotFoundException {
        return (Packet) dataIn.readObject();
    }

    public void send(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.type.toString() + "...");

        try {

            dataOut.writeObject(packetIn);
            dataOut.flush();

        } catch (IOException e) {
            pluginInstance.error("That packed failed to send :(");
            e.printStackTrace();

        }
    }
}
