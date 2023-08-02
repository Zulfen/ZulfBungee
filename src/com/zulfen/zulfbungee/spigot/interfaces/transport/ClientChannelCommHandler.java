package com.zulfen.zulfbungee.spigot.interfaces.transport;

import com.comphenix.protocol.ProtocolManager;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.handlers.protocol.ChannelPayload;
import com.zulfen.zulfbungee.spigot.interfaces.ClientCommHandler;
import com.zulfen.zulfbungee.spigot.socket.factory.ChannelConnectionFactory;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketChunk;
import com.zulfen.zulfbungee.universal.socket.objects.ZulfByteBuffer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

public class ClientChannelCommHandler extends ClientCommHandler<ChannelConnectionFactory> {

    private final ChannelPayload channelPayload;
    private boolean transferFinished = false;

    private final int maxPacketSize;

    private final ByteArrayOutputStream fullPacketBytes = new ByteArrayOutputStream();

    public ClientChannelCommHandler(ZulfBungeeSpigot pluginInstanceIn, int compressPackets) {
        super(pluginInstanceIn);
        pluginInstance.getServer().getMessenger().registerOutgoingPluginChannel(pluginInstance, "zproxy:channel");
        ProtocolManager protocolManager = pluginInstanceIn.getProtocolManager();
        this.channelPayload = new ChannelPayload(this, protocolManager.getMinecraftVersion());
        protocolManager.addPacketListener(channelPayload);
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
                    pluginInstance.error(packetIn.getType().name());
                    queueIn.offer(Optional.of(packetIn));
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            pluginInstance.error("Error trying to deserialize packet for plugin messaging!:");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Packet> readPacket() {
        try {
            return queueIn.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    private void sendBytes(byte[] toSend) {

        Collection<? extends Player> onlinePlayers = pluginInstance.getServer().getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            try {
                awaitProperConnection.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (pluginInstance.isEnabled()) {
            pluginInstance.getServer().sendPluginMessage(pluginInstance, "zproxy:channel", toSend);
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


    @Override
    public synchronized void writePacket(Packet inputPacket) {

        byte[] fullPacketBytes = packetToBytes(inputPacket);

        if (fullPacketBytes.length > 20480) {

            try (ByteArrayInputStream packetBytes = new ByteArrayInputStream(fullPacketBytes)) {

                byte[] newBytesOut = new byte[maxPacketSize];
                while (packetBytes.read(newBytesOut, 0, maxPacketSize) != -1) {
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
    public void destroy() {
        pluginInstance.getProtocolManager().removePacketListener(channelPayload);
        super.destroy();
    }

}
