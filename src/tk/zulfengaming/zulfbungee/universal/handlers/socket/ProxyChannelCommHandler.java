package tk.zulfengaming.zulfbungee.universal.handlers.socket;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyCommHandler;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.*;
import java.util.Optional;

public class ProxyChannelCommHandler<P, T> extends ProxyCommHandler<P, T> {

    private final MessageCallback messageCallback;

    public ProxyChannelCommHandler(ZulfBungeeProxy<P, T> pluginInstanceIn, MessageCallback messageCallbackIn) {
        super(pluginInstanceIn);
        this.messageCallback = messageCallbackIn;
    }

    public void provideBytes(byte[] dataIn) {

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataIn);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            Object readObject = objectInputStream.readObject();

            if (readObject instanceof Packet) {
                Packet packetIn = (Packet) readObject;
                queueIn.offer(Optional.of(packetIn));
            }

        } catch (IOException | ClassNotFoundException e) {
            pluginInstance.error("Error trying to deserialize packet for plugin messaging!:");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Packet> readPacket() {
        try {
            Optional<Packet> take = queueIn.take();
            pluginInstance.error("Recieved from input: " + take);
            return take;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    protected void writePacket(Packet toWrite) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(toWrite);
            objectOutputStream.flush();

            boolean hasSent = messageCallback.sendData(byteArrayOutputStream.toByteArray());

            if(!hasSent) {
                connection.destroy();
            }

            pluginInstance.error("Written: " + toWrite);

        } catch (IOException e) {
            pluginInstance.error("Error trying to serialise packet for plugin messaging!:");
            e.printStackTrace();
        }

    }



}
