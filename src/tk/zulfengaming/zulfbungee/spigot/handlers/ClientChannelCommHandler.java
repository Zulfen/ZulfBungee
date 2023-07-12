package tk.zulfengaming.zulfbungee.spigot.handlers;

import com.google.common.collect.Iterables;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientCommHandler;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

public class ClientChannelCommHandler extends ClientCommHandler implements PluginMessageListener {

    private String channelName;

    public ClientChannelCommHandler(ZulfBungeeSpigot pluginInstanceIn) {
        super(pluginInstanceIn);
    }

    // connection can't be used in the constructor, just overriding the start method for now.
    @Override
    public void start() {

        String forcedName = connection.getForcedName();

        if (!forcedName.isEmpty()) {
            pluginInstance.getServer().getMessenger().registerIncomingPluginChannel(pluginInstance, "zproxy:channel", this);
            pluginInstance.getServer().getMessenger().registerOutgoingPluginChannel(pluginInstance, "zproxy:channel");
        } else {
            connection.destroy();
            throw new RuntimeException("Could not find a set connection name in the config! Shutting down this connection.");
        }

        super.start();

    }

    @Override
    protected Optional<Packet> readPacket() {
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


            pluginInstance.getTaskManager().newRepeatingTask(new BukkitRunnable() {
                @Override
                public void run() {
                    Collection<? extends Player> onlinePlayers = pluginInstance.getServer().getOnlinePlayers();
                    Player randomPlayer = Iterables.getFirst(onlinePlayers, null);
                    if (randomPlayer == null) {
                        return;
                    }
                    randomPlayer.sendPluginMessage(pluginInstance, "zproxy:channel", byteArrayOutputStream.toByteArray());
                    cancel();
                }
            }, 20);

        } catch (IOException e) {
            pluginInstance.error("Error trying to serialize packet for plugin messaging!:");
            e.printStackTrace();
        }


    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        pluginInstance.error("Hai");

        if (channel.equals("zproxy:channel")) {

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message);
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

        } else {
            pluginInstance.logDebug("Channel: " + channel);
        }


    }
}
