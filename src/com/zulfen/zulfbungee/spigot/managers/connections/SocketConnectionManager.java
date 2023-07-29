package com.zulfen.zulfbungee.spigot.managers.connections;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.spigot.socket.factory.SocketConnectionFactory;
import com.zulfen.zulfbungee.spigot.tasks.SocketConnectionTask;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SocketConnectionManager extends ConnectionManager<SocketConnectionFactory> implements Runnable {

    private final LinkedBlockingQueue<Optional<Packet>> connectionPackets = new LinkedBlockingQueue<>();

    private final AtomicInteger registered = new AtomicInteger();
    private final Semaphore connectionBarrier = new Semaphore(0);

    private final SocketConnectionTask connectionTask;

    public SocketConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort) {
        super(pluginIn, SocketConnectionFactory.class);
        this.connectionTask = new SocketConnectionTask(this, clientAddress, clientPort, serverAddress, serverPort);
    }

    private Queue<Packet> sendGetPacketList(Packet packetIn) {

        Queue<Packet> packetQueue = new LinkedList<>();
        if (sendDirect(packetIn)) {
            for (int i = 0; i < registered.get(); i++) {
                try {
                    Optional<Packet> take = connectionPackets.take();
                    take.ifPresent(packetQueue::offer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return packetQueue;

    }

    @Override
    protected void sendDirectImpl(Packet packetIn) {
        allConnections.forEach(connection -> connection.sendDirect(packetIn));
    }

    @Override
    public Optional<Packet> send(Packet packetIn) {
        Queue<Packet> queue = sendGetPacketList(packetIn);
        return Optional.ofNullable(queue.poll());
    }

    @Override
    public List<ClientPlayer> getPlayers(ClientServer[] serversIn) {

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

                connectionPackets.clear();

                while (registered.get() > 0) {

                    for (Connection<SocketConnectionFactory> connection : allConnections) {

                        try {

                            Optional<Packet> getPacket = connection.read();
                            connectionPackets.put(getPacket);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                    }

                }

                connectionPackets.put(Optional.empty());

                taskManager.newAsyncTask(connectionTask);
                connectionBarrier.acquire();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } while(running.get());

        connectionPackets.offer(Optional.empty());

    }

    public void releaseConnectionBarrier() {
        connectionBarrier.release();
    }

    public void registerBefore() {
        registered.incrementAndGet();
    }

    @Override
    public void deRegister(Connection<SocketConnectionFactory> connectionIn) {
        registered.decrementAndGet();
        super.deRegister(connectionIn);
    }

    public void shutdown() {
        connectionBarrier.release();
        super.shutdown();
    }

}
