package tk.zulfengaming.bungeesk.spigot.socket;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.socket.PacketHandlerManager;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import static tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot.*;

public class ClientConnection implements Runnable {

    // plugin instance ?
    BungeeSkSpigot instance;

    Socket socket;

    public String address;
    public int port;
    public String name;

    // handling packets
    PacketHandlerManager packetManager;

    // direct access to IO
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;

    public ClientConnection(String address, int port) {
        this.instance = BungeeSkSpigot.getPlugin();

    }

    public void run() {
        try {
            this.socket = new Socket(address, port);

            this.dataIn = new ObjectInputStream(socket.getInputStream());
            this.dataOut = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            log("There was an error while handling data from the server!");
            e.printStackTrace();
        }
    }

    private Packet convertPacket(Object object) {
        if (object instanceof Packet) {
            return (Packet) object;
        } else {
            log("Packet received, but does not appear to be valid. Ignoring it.");
            return null;
        }
    }

    public Object read() throws IOException, ClassNotFoundException {
        return dataIn.readObject();
    }

    public void send(Packet packetIn) {
        log("Sending packet " + packetIn.type.toString() + "...");
        try {
            dataOut.writeObject(packetIn);
        } catch (IOException e) {
            log("That packed failed to send :(");
            e.printStackTrace();
        }
    }

    public void end() {
        socket.close();
    }
}
