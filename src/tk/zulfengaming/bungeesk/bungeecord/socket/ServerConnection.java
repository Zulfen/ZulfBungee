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
    private BungeeSkProxy pluginInstance;

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
        this.socket = serverIn.getSocket();
        this.packetManager = serverIn.getPacketManager();
        this.pluginInstance = serverIn.getPluginInstance();
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

                    if (!(processedPacket == null) && packetIn.isReturnable()) {
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

            disconnect();

            e.printStackTrace();
        }

    }

    public void disconnect()  {
        pluginInstance.log("Disconnecting client " + address);

        running = false;

        try {
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            pluginInstance.error("Error closing data streams in a server connection:");

            e.printStackTrace();
        }

        server.removeConnection(this);

    }

    public Packet read() throws IOException, ClassNotFoundException {
        return (Packet) dataIn.readObject();
    }

    public void send(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "...");

        try {

            dataOut.writeObject(packetIn);
            dataOut.flush();

        } catch (IOException e) {
            pluginInstance.error("That packed failed to send :(");
            e.printStackTrace();

        }
    }

    public Server getServer() {
        return server;
    }

    public BungeeSkProxy getPluginInstance() {
        return pluginInstance;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public boolean isRunning() {
        return running;
    }
}
