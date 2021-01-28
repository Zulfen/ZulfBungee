package tk.zulfengaming.bungeesk.spigot.socket;

import org.json.simple.JSONObject;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class ClientConnection implements Runnable {

    public static ClientConnection clientConnection;

    // plugin instance ?
    public BungeeSkSpigot instance;

    public Socket socket;

    public InetAddress clientAddress;
    public SocketAddress serverSocketAddress;

    public int clientPort;
    public String name;

    public boolean running;

    // handling packets
    PacketHandlerManager packetManager;

    // direct access to IO
    public ObjectInputStream dataIn;
    public ObjectOutputStream dataOut;

    public ClientConnection(BungeeSkSpigot instance, InetAddress addressIn, int portIn) {
        this.instance = instance;
        this.clientAddress = addressIn;
        this.clientPort = portIn;
        instance.log(String.valueOf(clientPort));

        this.packetManager = new PacketHandlerManager(this);
    }

    public void connect() {

        try {

            socket = new Socket(InetAddress.getByName(instance.config.getString("server-address")), instance.config.getInt("server-port"));

            dataIn = new ObjectInputStream(socket.getInputStream());
            dataOut = new ObjectOutputStream(socket.getOutputStream());

            send_direct(new Packet(new InetSocketAddress(clientAddress, clientPort), "test", PacketTypes.HANDSHAKE, new JSONObject(), true));

            running = true;

        } catch (IOException e) {

            instance.error("There was an error trying connect to the endpoint. Retrying...");
            e.printStackTrace();
        }

    }

    public void run() {
        try {

            connect();

            while (running) {

                while (running && socket.isConnected()) {

                    if (dataIn.readObject() instanceof Packet) {
                        Packet packetIn = (Packet) dataIn.readObject();

                        Packet processedPacket = packetManager.handlePacket(packetIn, serverSocketAddress);

                        if (!(processedPacket == null) && packetIn.returnable) {
                            send(processedPacket);

                        }
                    } else {
                        instance.warning("Packet received from " + serverSocketAddress + ", but does not appear to be valid. Ignoring it.");
                    }
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            instance.error("There was an error while handling data from the server!");
            e.printStackTrace();
        }
    }

    // methods below are static as there is only once instance of this class ever created

    public Packet read() throws IOException, ClassNotFoundException {
        return (Packet) dataIn.readObject();
    }

    public void send_direct(Packet packetIn) {
        instance.log("Sending packet " + packetIn.type.toString() + "...");
        try {
            dataOut.writeObject(packetIn);
            dataOut.flush();

        } catch (IOException e) {
            instance.error("That packed failed to send :(");
            e.printStackTrace();
        }
    }

    public Packet send(Packet packetIn) throws IOException, ClassNotFoundException {
        send_direct(packetIn);

        return read();
    }

    public void end() throws IOException {

        instance.log("Shutting down MainServer...");

        socket.close();

        running = false;

    }

    public void restart() {

        instance.log("Restarting server...");

        try {
            end();
            connect();

        } catch (IOException e) {
            instance.error("Something went wrong trying to restart. You are utterly fucked, i'm sorry.");
            e.printStackTrace();
        }

    }

    // this is a singleton. yes, i am aware other classes are like this
    public static ClientConnection getClientConnection() {
        return clientConnection;
    }
}
