package tk.zulfengaming.zulfbungee.spigot.managers.connections;

import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;
import tk.zulfengaming.zulfbungee.spigot.tasks.SocketConnectionTask;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SocketConnectionManager extends ConnectionManager implements Runnable {

    private final AtomicInteger registered = new AtomicInteger();
    private final LinkedBlockingQueue<Optional<Packet>> connectionPackets = new LinkedBlockingQueue<>();

    private final Semaphore connectionBarrier = new Semaphore(0);
    private final CopyOnWriteArrayList<SocketConnection> allConnections = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SocketAddress> blockedConnections = new CopyOnWriteArrayList<>();

    private final SocketConnectionTask connectionTask;

    public SocketConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort) {
        super(pluginIn);
        this.connectionTask = new SocketConnectionTask(this, connectionBarrier, clientAddress, clientPort, serverAddress, serverPort);
    }

    @Override
    public void sendDirect(Packet packetIn) {
        allConnections.forEach(connection -> connection.sendDirect(packetIn));
    }

    private Queue<Packet> sendGetPacketList(Packet packetIn) {

        Queue<Packet> packetQueue = new LinkedList<>();

        if (registered.get() > 0) {

            sendDirect(packetIn);
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

                    for (Connection connection : allConnections) {

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

    public boolean isBlocked(SocketAddress addressIn) {
        return blockedConnections.contains(addressIn);
    }

    public void register() {
        registered.incrementAndGet();
    }

    public void deRegister() {
        registered.decrementAndGet();
    }

    public void blockConnection(Connection connectionIn) {
        blockedConnections.add(connectionIn.getAddress());
        connectionIn.destroy();
    }

    public void newSocketConnection(Socket socketIn) throws IOException {
        SocketConnection socketConnection = new SocketConnection(this, socketIn);
        allConnections.add(socketConnection);
        taskManager.newAsyncTask(socketConnection);
    }

    public void shutdown() {
        connectionBarrier.release();
        for (SocketConnection connection : allConnections) {
            connection.destroy();
        }
        super.shutdown();
    }

}
