package tk.zulfengaming.zulfbungee.universal.handlers.socket;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyCommHandler;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class ProxySocketCommHandler<P, T> extends ProxyCommHandler<P, T> {

    private final Socket socket;

    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public ProxySocketCommHandler(ZulfBungeeProxy<P, T> instanceIn, Socket socketIn) throws IOException {
        super(instanceIn);
        this.socket = socketIn;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    protected Optional<Packet> readPacket() {

        try {
            Object readObject = inputStream.readObject();
            if (readObject instanceof Packet) {
                return Optional.of((Packet) readObject);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            destroy();
        }

        return Optional.empty();

    }

    @Override
    protected void writePacket(Packet toWrite) {
        try {
            outputStream.writeObject(toWrite);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            destroy();
        }
    }

    @Override
    protected void freeResources() {
        try {
            socket.close();
        } catch (IOException e) {
            pluginInstance.error("Error closing socket on connection " + connection.getAddress());
            e.printStackTrace();
        }
    }

}
