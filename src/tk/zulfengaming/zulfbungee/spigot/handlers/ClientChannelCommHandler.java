package tk.zulfengaming.zulfbungee.spigot.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.protocol.ChannelPayload;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientCommHandler;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

public class ClientChannelCommHandler extends ClientCommHandler {

    private final ChannelPayload channelPayload;

    public ClientChannelCommHandler(ZulfBungeeSpigot pluginInstanceIn) {
        super(pluginInstanceIn);
        pluginInstance.getServer().getMessenger().registerOutgoingPluginChannel(pluginInstance, "zproxy:channel");
        this.channelPayload = new ChannelPayload(this);
        pluginInstance.getProtocolManager().addPacketListener(channelPayload);

    }

    public void provideBytes(byte[] bytesIn) {

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesIn);
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
            return queueIn.take();
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

            Collection<? extends Player> onlinePlayers = pluginInstance.getServer().getOnlinePlayers();

            if (onlinePlayers.isEmpty()) {
                pluginInstance.acquireChannelWait();
            }

            if (pluginInstance.isEnabled()) {
                pluginInstance.getServer().sendPluginMessage(pluginInstance, "zproxy:channel", byteArrayOutputStream.toByteArray());
            }

        } catch (IOException e) {
            pluginInstance.error("Error trying to serialize packet for plugin messaging!:");
            e.printStackTrace();
        }


    }

    @Override
    public void destroy() {
        pluginInstance.getProtocolManager().removePacketListener(channelPayload);
        super.destroy();
    }

}
