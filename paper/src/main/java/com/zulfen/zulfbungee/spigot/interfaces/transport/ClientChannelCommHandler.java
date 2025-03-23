package com.zulfen.zulfbungee.spigot.interfaces.transport;

import com.zulfen.zulfbungee.spigot.handlers.protocol.ChannelPayload;
import com.zulfen.zulfbungee.spigot.interfaces.ClientCommHandler;
import com.zulfen.zulfbungee.spigot.socket.ClientChannelConnection;
import com.zulfen.zulfbungee.spigot.socket.factory.ChannelConnectionFactory;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketChunk;
import com.zulfen.zulfbungee.core.socket.objects.ZulfByteBuffer;
import com.zulfen.zulfbungee.core.util.MultiplePacketQueue;

import java.io.*;
import java.util.Optional;

public class ClientChannelCommHandler extends ClientCommHandler<ChannelConnectionFactory> {

    private final ChannelPayload channelPayload;
    private boolean transferFinished = false;

    private final MultiplePacketQueue incomingPackets = new MultiplePacketQueue();
    private final int maxPacketSize;

    private final ByteArrayOutputStream fullPacketBytes = new ByteArrayOutputStream();

    public ClientChannelCommHandler(ClientChannelConnection connectionIn, int compressPackets) {
        super(connectionIn);
        pluginInstance.getServer().getMessenger().registerOutgoingPluginChannel(pluginInstance, "zproxy:channel");
        this.channelPayload = new ChannelPayload(this, pluginInstance.getProtocolManager());
        this.maxPacketSize = compressPackets;
    }

    public void provideBytes(byte[] bytesIn) {

        if (transferFinished) {
            fullPacketBytes.reset();
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesIn);
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
                    incomingPackets.enqueue(packetIn);
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            pluginInstance.error("Error trying to deserialize packet for plugin messaging!:");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Packet> readPacketImpl() {
        return incomingPackets.take(false);
    }


    private void prepareMessage(byte[] toSend) {
        pluginInstance.getServer().getOnlinePlayers()
                .stream()
                .findFirst()
                .ifPresent(player -> channelPayload.sendCustomPayload(player, toSend));
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


    @Override
    public void writePacketImpl(Packet inputPacket) {

        byte[] fullPacketBytes = packetToBytes(inputPacket);

        if (fullPacketBytes.length > 5120) {

            try (ByteArrayInputStream packetBytes = new ByteArrayInputStream(fullPacketBytes)) {

                byte[] newBytesOut = new byte[maxPacketSize];
                while (packetBytes.read(newBytesOut, 0, maxPacketSize) != -1) {
                    PacketChunk packetChunk = new PacketChunk(inputPacket.getType(), new ZulfByteBuffer(newBytesOut),
                            false);
                    prepareMessage(packetToBytes(packetChunk));
                }
                prepareMessage(packetToBytes(new PacketChunk(inputPacket.getType(), ZulfByteBuffer.emptyBuffer(), true)));

            } catch (IOException e) {
                throw new RuntimeException("Error whilst sending packet chunks:", e);
            }

        } else {
            prepareMessage(fullPacketBytes);
        }



    }

    @Override
    protected void freeResources() {}

    @Override
    public void destroy() {
        pluginInstance.getProtocolManager().getAsynchronousManager().unregisterAsyncHandler(channelPayload);
        pluginInstance.getServer().getMessenger().unregisterOutgoingPluginChannel(pluginInstance);
        incomingPackets.notifyShutdown();
        super.destroy();
    }

}
