package com.zulfen.zulfbungee.spigot.interfaces.transport;

import com.zulfen.zulfbungee.spigot.handlers.protocol.ChannelPayload;
import com.zulfen.zulfbungee.spigot.interfaces.ClientCommHandler;
import com.zulfen.zulfbungee.spigot.socket.ClientChannelConnection;
import com.zulfen.zulfbungee.spigot.socket.factory.ChannelConnectionFactory;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketChunk;
import com.zulfen.zulfbungee.universal.socket.objects.ZulfByteBuffer;
import com.zulfen.zulfbungee.universal.util.BlockingPacketQueue;

import java.io.*;
import java.util.Optional;

public class ClientChannelCommHandler extends ClientCommHandler<ChannelConnectionFactory> {

    private final ChannelPayload channelPayload;
    private boolean transferFinished = false;

    private final BlockingPacketQueue incomingPackets = new BlockingPacketQueue();
    private final int maxPacketSize;

    private final ByteArrayOutputStream fullPacketBytes = new ByteArrayOutputStream();

    public ClientChannelCommHandler(ClientChannelConnection connectionIn, int compressPackets) {
        super(connectionIn);
        pluginInstance.getServer().getMessenger().registerOutgoingPluginChannel(pluginInstance, "zproxy:channel");
        this.channelPayload = new ChannelPayload(this, pluginInstance.getProtocolManager());
        this.maxPacketSize = compressPackets;
    }

    public synchronized void provideBytes(byte[] bytesIn) {

        if (transferFinished) {
            fullPacketBytes.reset();
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesIn);
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
                    incomingPackets.offer(packetIn);
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
        pluginInstance.getServer().sendPluginMessage(pluginInstance, "zproxy:channel", toSend);
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

        if (fullPacketBytes.length > 20480) {

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
    public void destroy() {
        pluginInstance.getProtocolManager().removePacketListener(channelPayload);
        pluginInstance.getServer().getMessenger().unregisterOutgoingPluginChannel(pluginInstance);
        incomingPackets.notifyListeners();
        super.destroy();
    }

}
