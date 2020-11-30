package tk.zulfengaming.bungeesk.bungeecord.socket;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.scheduler.BungeeTask;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketHandlerManager;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

import static tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot.*;

public class ServerConnection implements Runnable {

    // plugin instance ?
    BungeeSkProxy instance;

    Socket socket;

    public SocketAddress address;
    public String name;

    // handling packets
    PacketHandlerManager packetManager;

    // direct access to IO
    private ObjectInputStream dataIn;
    private ObjectOutputStream dataOut;

    public ServerConnection(Server serverIn) {
        this.socket = serverIn.socket;
        this.packetManager = serverIn.packetManager;
        this.instance = serverIn.instance;

        this.address = socket.getRemoteSocketAddress();


    }

    public void connection() {
        try {
            this.dataIn = new ObjectInputStream(socket.getInputStream());
            this.dataOut = new ObjectOutputStream(socket.getOutputStream());

            Packet packetIn = convertPacket(dataIn.readObject());

            if(!(packetIn == null)) {
                Packet processedPacket = (Packet) packetManager.handlePacket(packetIn);

                if (!(processedPacket == null) && packetIn.returnable) {
                    send(processedPacket);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            log("There was an error while handling data for a connection!");
            e.printStackTrace();
        }
    }

    private Packet convertPacket(Object object) {
        if (object instanceof Packet) {
            return (Packet) object;
        }
        return null;
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

    @Override
    public void run() {
        connection();
    }
}
