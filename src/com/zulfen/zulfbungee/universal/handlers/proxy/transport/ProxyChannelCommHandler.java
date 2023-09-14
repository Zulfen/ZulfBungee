package com.zulfen.zulfbungee.universal.handlers.proxy.transport;

import com.zulfen.zulfbungee.universal.handlers.proxy.ProxyCommHandler;
import com.zulfen.zulfbungee.universal.interfaces.MessageCallback;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketChunk;
import com.zulfen.zulfbungee.universal.socket.objects.ZulfByteBuffer;
import com.zulfen.zulfbungee.universal.socket.transport.ChannelServerConnection;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.SynchronousQueue;

public class ProxyChannelCommHandler<P, T> extends ProxyCommHandler<P, T> {

    private final MessageCallback messageCallback;

    private final SynchronousQueue<Packet> incomingQueue = new SynchronousQueue<>();
    private final ByteArrayOutputStream fullPacketBytes = new ByteArrayOutputStream();
    private boolean transferFinished = false;

    public ProxyChannelCommHandler(ChannelServerConnection<P, T> connectionIn, MessageCallback messageCallbackIn) {
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

                if (readObject instanceof PacketChunk) {

                    PacketChunk packetChunk = (PacketChunk) readObject;

                    if (packetChunk.isFinalChunk()) {
                        transferFinished = true;
                        provideBytes(fullPacketBytes.toByteArray());
                    } else {
                        transferFinished = false;
                        byte[] chunkData = packetChunk.getDataSingle().getData();
                        fullPacketBytes.write(chunkData);
                    }

                } else {
                    Packet packetIn = (Packet) readObject;
                    incomingQueue.offer(packetIn);
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            pluginInstance.error("Error trying to deserialize packet for plugin messaging!:");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Packet> readPacketImpl() {
        try {
            return Optional.of(incomingQueue.take());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
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

        if (fullPacketBytes.length > 20480) {

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



}
