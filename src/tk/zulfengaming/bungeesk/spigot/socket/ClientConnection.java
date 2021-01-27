package tk.zulfengaming.bungeesk.spigot.socket;

import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class ClientConnection implements Runnable {

    public static ClientConnection clientConnection;

    // plugin instance ?
    protected BungeeSkSpigot instance;

    public Socket socket;

    public InetAddress clientAddress;
    public SocketAddress serverSocketAddress;

    public int port;
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
        this.port = portIn;

        try {
            this.serverSocketAddress = new InetSocketAddress(InetAddress.getByName(instance.config.getString("server-address")), instance.config.getInt("server-port"));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void connect() {

        try {
            socket.connect(serverSocketAddress);

            running = true;

        } catch (IOException e) {

            instance.error("There was an error trying connect to the endpoint:");
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            this.socket = new Socket(clientAddress, port);

            dataIn = new ObjectInputStream(socket.getInputStream());
            dataOut = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            instance.error("There was an error while handling data from the server!");
            e.printStackTrace();
        }
    }

    public void end() throws IOException {
        socket.close();
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

    // this is a singleton. yes, i am aware other classes are like this
    public static ClientConnection getClientConnection() {
        return clientConnection;
    }
}
