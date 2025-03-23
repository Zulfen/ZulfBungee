package com.zulfen.zulfbungee.core.handlers.proxy.transport;

import com.zulfen.zulfbungee.core.handlers.proxy.ProxyCommHandler;
import com.zulfen.zulfbungee.core.interfaces.MessageCallback;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketChunk;
import com.zulfen.zulfbungee.core.socket.objects.ZulfByteBuffer;
import com.zulfen.zulfbungee.core.socket.transport.ChannelServerConnection;
import com.zulfen.zulfbungee.core.util.SinglePacketQueue;

import java.io.*;
import java.util.Optional;

public class ProxyChannelCommHandler<P, T, C> extends ProxyCommHandler<P, T, C> {

    private final MessageCallback messageCallback;

    private final SinglePacketQueue incomingQueue = new SinglePacketQueue();
    private final ByteArrayOutputStream fullPacketBytes = new ByteArrayOutputStream();
    private boolean transferFinished = false;

    public ProxyChannelCommHandler(ChannelServerConnection<P, T, C> connectionIn, MessageCallback messageCallbackIn) {
        super(connectionIn);
        this.messageCallback = messageCallbackIn;
    }

    public void provideBytes(byte[] dataIn) {

        if (transferFinished) {
            fullPacketBytes.reset();
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataIn);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            Object readObject = objectInputStream.readObject();

            if (readObject instanceof Packet) {

                if (readObject instanceof PacketChunk packetChunk) {

                    if (packetChunk.isFinalChunk()) {
                        transferFinished = true;
                        provideBytes(fullPacketBytes.toByteArray());
                    } else {
                        transferFinished = false;
                        byte[] chunkData = packetChunk.getDataSingle().data();
                        fullPacketBytes.write(chunkData);
                    }

                } else {
                    Packet packetIn = (Packet) readObject;
                    pluginInstance.warning("Constructed " + packetIn);
                    incomingQueue.enqueue(packetIn);
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            pluginInstance.error("Error trying to deserialize packet for plugin messaging!:");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Packet> readPacketImpl() {
        return incomingQueue.take(false);
    }

    private void sendBytes(byte[] dataIn) {

        boolean hasSent = messageCallback.sendData(dataIn);

        if(!hasSent) {
            packetConsumer.destroyConsumer();
        }

    }

    private byte[] packetToBytes(Packet inputPacket) {

        try (ByteArrayOutputStream packetBytesOut = new ByteArrayOutputStream();
             ObjectOutputStream packetOut = new ObjectOutputStream(packetBytesOut)) {

            packetOut.writeObject(inputPacket);
            packetOut.flush();

            return packetBytesOut.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error trying to serialize packet for plugin messaging!:", e);
        }

    }

    public void writePacketImpl(Packet inputPacket) {

        byte[] fullPacketBytes = packetToBytes(inputPacket);

        if (fullPacketBytes.length > 5120) {

            try (ByteArrayInputStream fullByteStream = new ByteArrayInputStream(fullPacketBytes)) {

                byte[] newBytesOut = new byte[5120];
                while (fullByteStream.read(newBytesOut, 0, 5120) != -1) {
                    PacketChunk packetChunk = new PacketChunk(inputPacket.getType(), new ZulfByteBuffer(newBytesOut),
                            false);
                    sendBytes(packetToBytes(packetChunk));
                }
                sendBytes(packetToBytes(new PacketChunk(inputPacket.getType(), new ZulfByteBuffer(new byte[0]), true)));


            } catch (IOException e) {
                throw new RuntimeException("Error whilst sending packet chunks:", e);
            }

        } else {
            sendBytes(fullPacketBytes);
        }



    }

    @Override
    protected void freeResources() {
        queueIn.notifyShutdown();
    }


}
