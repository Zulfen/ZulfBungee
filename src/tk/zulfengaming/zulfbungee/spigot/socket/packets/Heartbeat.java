package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.spigot.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.net.SocketAddress;

public class Heartbeat extends PacketHandler {

    public Heartbeat(Connection connectionIn) {
        super(connectionIn, PacketTypes.HEARTBEAT);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {
        HeartbeatTask heartbeatTask = getConnection().getConnectionManager().getHeartbeatTask();
        heartbeatTask.setPing(System.currentTimeMillis() - heartbeatTask.getTimeBefore());
    }
}
