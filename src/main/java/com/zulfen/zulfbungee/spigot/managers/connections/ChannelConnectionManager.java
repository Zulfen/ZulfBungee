package com.zulfen.zulfbungee.spigot.managers.connections;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.ClientChannelConnection;
import com.zulfen.zulfbungee.spigot.socket.factory.ChannelConnectionFactory;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import org.bukkit.ChatColor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChannelConnectionManager extends ConnectionManager<ChannelConnectionFactory> {

    private ClientChannelConnection clientChannelConnection;

    private final SocketAddress socketAddress;

    public ChannelConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress serverAddress, int serverPort) {
        super(pluginIn, ChannelConnectionFactory.class);
        this.socketAddress = new InetSocketAddress(serverAddress, serverPort);
        pluginInstance.logInfo(ChatColor.GREEN + "Waiting for a player to join...");
    }

    @Override
    protected boolean sendDirectImpl(Packet packetIn) {
        return clientChannelConnection.sendDirect(packetIn);
    }

    @Override
    public synchronized Optional<Packet> send(Packet packetIn) {
        boolean sendDirect = sendDirect(packetIn);
        if (sendDirect) {
            Optional<Packet> read = clientChannelConnection.readSkriptQueue();
            if (!read.isPresent()) {
                pluginInstance.logDebug(String.format("%sDropped packet %s due to no response from proxy.", ChatColor.YELLOW, packetIn.getType().name()));
            }
            return read;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized List<ClientPlayer> getPlayers(ClientServer[] serversIn) {

        Optional<Packet> send = send(new Packet(PacketTypes.PROXY_PLAYERS,
                true, false, serversIn));

        if (send.isPresent()) {
            Packet packet = send.get();
            return Arrays.stream(packet.getDataArray())
                    .filter(ClientPlayer.class::isInstance)
                    .map(ClientPlayer.class::cast)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();

    }

    public void newChannelConnection() {

        if (clientChannelConnection != null) {
            clientChannelConnection.destroy();
        }

        clientChannelConnection = createNewConnection()
                .withAddress(socketAddress)
                .compressLargePacketTo(5120)
                .build();

        clientChannelConnection.start();

    }

    public void signalAvailableConnection() {

        if (clientChannelConnection == null) {
            newChannelConnection();
        }

        clientChannelConnection.getClientCommHandler().signalInitialConnection();

    }

    @Override
    public void shutdown() {
        clientChannelConnection.destroy();
        super.shutdown();
    }

}
