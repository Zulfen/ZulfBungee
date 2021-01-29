package tk.zulfengaming.bungeesk.spigot.socket;

import org.json.simple.JSONObject;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
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

        try {
            this.serverSocketAddress = new InetSocketAddress(InetAddress.getByName(instance.config.getString("server-address")), instance.config.getInt("server-port"));
        } catch (UnknownHostException e) {
            instance.warning("Couldn't find hostname for specified address.");
        }

        this.packetManager = new PacketHandlerManager(this);
    }

    public void connect() throws IOException {


        socket = new Socket();

        try {

            instance.taskManager.newRepeatingTask(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {

                    }

            }, "ConnectionConnecting", 100);

        } catch (TaskAlreadyExists e) {

            instance.error("There was an error trying connect to the endpoint. :(");

            e.printStackTrace();
            notify();
        }



    }

    public void run() {
        try {

            connect();

            dataIn = new ObjectInputStream(socket.getInputStream());
            dataOut = new ObjectOutputStream(socket.getOutputStream());

            send_direct(new Packet(new InetSocketAddress(clientAddress, clientPort), "test", PacketTypes.HANDSHAKE, new JSONObject(), true));

            while (running) {

                if (dataIn.readObject() instanceof Packet) {
                    instance.log("uwu");
                    Packet packetIn = (Packet) dataIn.readObject();

                    Packet processedPacket = packetManager.handlePacket(packetIn, serverSocketAddress);

                    if (!(processedPacket == null) && packetIn.returnable) {
                        send_direct(processedPacket);

                    }
                } else {
                    instance.warning("Packet received from " + serverSocketAddress + ", but does not appear to be valid. Ignoring it.");
                }
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            instance.error("There was an error while handling data from the server!");
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


    // this is a singleton. yes, i am aware other classes are like this
    public static ClientConnection getClientConnection() {
        return clientConnection;
    }
}
