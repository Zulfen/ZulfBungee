package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.spigot.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.LinkedList;


public class PacketHandlerManager {

    private final LinkedList<PacketHandler> handlers = new LinkedList<>();

    public PacketHandlerManager(ClientConnection connectionIn) {
        handlers.addLast(new Heartbeat(connectionIn));
        handlers.addLast(new ClientHandshake(connectionIn));
        handlers.addLast(new ServerSwitchEvent(connectionIn));
        handlers.addLast(new ServerMessageEvent(connectionIn));
        handlers.addLast(new PlayerDisconnectEvent(connectionIn));
        handlers.addLast(new PlayerConnectEvent(connectionIn));
        handlers.addLast(new InvalidConfiguration(connectionIn));
        handlers.addLast(new ClientInfo(connectionIn));
        handlers.addLast(new ClientDisconnect(connectionIn));
    }

    public LinkedList<PacketHandler> getHandlers() {
        return handlers;
    }

    public PacketHandler getHandler(Packet packetIn) {

        for (PacketHandler packetHandler : handlers) {
            for (PacketTypes type : packetHandler.getTypes()) {
                if (type == packetIn.getType()) {
                    return packetHandler;
                }
            }
        }
        return null;
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public void handlePacket(Packet packetIn, SocketAddress address) {
        getHandler(packetIn).handlePacket(packetIn, address);
    }
}
