package com.zulfen.zulfbungee.spigot.managers.connections;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.spigot.socket.factory.SocketConnectionFactory;
import com.zulfen.zulfbungee.spigot.tasks.SocketConnectionTask;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.util.BlockingPacketQueue;
import org.bukkit.ChatColor;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SocketConnectionManager extends ConnectionManager<SocketConnectionFactory> implements Runnable {

    private final BlockingPacketQueue connectionPackets = new BlockingPacketQueue();

    private final AtomicInteger registered = new AtomicInteger();
    private final Semaphore connectionBarrier = new Semaphore(0);

    private final SocketConnectionTask connectionTask;

    public SocketConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort) {
        super(pluginIn, SocketConnectionFactory.class);
        this.connectionTask = new SocketConnectionTask(this, clientAddress, clientPort, serverAddress, serverPort);
    }

    private Queue<Packet> sendGetPacketList(Packet packetIn) {

        Queue<Packet> packetQueue = new LinkedList<>();
        boolean sendDirect = sendDirect(packetIn);
        if (sendDirect) {
            for (int i = 0; i < registered.get(); i++) {
                Optional<Packet> take = connectionPackets.take(true);
                if (take.isPresent()) {
                    packetQueue.offer(take.get());
                } else {
                    pluginInstance.logDebug(String.format("%sDropped packet %s due to no response from proxy.", ChatColor.YELLOW, packetIn.getType().name()));
                }
            }
        }

        return packetQueue;

    }

    @Override
    protected boolean sendDirectImpl(Packet packetIn) {
        return allConnections.stream()
                .allMatch(connection -> connection.sendDirect(packetIn));
    }

    @Override
    public synchronized Optional<Packet> send(Packet packetIn) {
        Queue<Packet> queue = sendGetPacketList(packetIn);
        return Optional.ofNullable(queue.poll());
    }

    @Override
    public synchronized List<ClientPlayer> getPlayers(ClientServer[] serversIn) {

        Queue<Packet> packets;

        if (serversIn.length > 0) {
            packets = sendGetPacketList(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, serversIn));
        } else {
            packets = sendGetPacketList(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, new Object[0]));
        }

        return packets.stream()
                .flatMap(packet -> Arrays.stream(packet.getDataArray()))
                .filter(ClientPlayer.class::isInstance)
                .map(ClientPlayer.class::cast)
                .collect(Collectors.toList());

    }

    @Override
    public void run() {

        do {

            try {

                while (registered.get() > 0) {

                    for (ClientConnection<SocketConnectionFactory> connection : allConnections) {
                        Optional<Packet> take = connection.readSkriptQueue();
                        take.ifPresent(connectionPackets::offer);
                    }

                }

                connectionPackets.notifyListeners();

                taskManager.newAsyncTask(connectionTask);
                connectionBarrier.acquire();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } while(running.get());

        connectionPackets.notifyListeners();

    }

    public void releaseConnectionBarrier() {
        connectionBarrier.release();
    }

    public void registerBefore() {
        registered.incrementAndGet();
    }

    @Override
    public void deRegister(ClientConnection<SocketConnectionFactory> connectionIn) {
        registered.decrementAndGet();
        super.deRegister(connectionIn);
    }

    public void shutdown() {
        connectionBarrier.release();
        super.shutdown();
    }

}
