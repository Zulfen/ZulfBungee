package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.LinkedList;


public class PacketHandlerManager {

    private final LinkedList<PacketHandler> handlers = new LinkedList<>();

    public PacketHandlerManager(Server serverIn) {
        handlers.addLast(new Heartbeat(serverIn));
        handlers.addLast(new ProxyPlayers(serverIn));
        handlers.addLast(new PlayerSendMessage(serverIn));
        handlers.addLast(new ClientHandshake(serverIn));
        handlers.addLast(new NetworkVariableModify(serverIn));
        handlers.addLast(new NetworkVariableGet(serverIn));
        handlers.addLast(new ServerOnline(serverIn));
        handlers.addLast(new GlobalServers(serverIn));
        handlers.addLast(new ClientInfo(serverIn));
        handlers.addLast(new ServerSendMessage(serverIn));
        handlers.addLast(new PlayerServer(serverIn));
    }

    public PacketHandler getHandler(Packet packetIn) {
        for (PacketHandler packetHandler : handlers)
            for (PacketTypes type : packetHandler.getTypes()) if (type == packetIn.getType()) return packetHandler;
        return null;
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        return getHandler(packetIn).handlePacket(packetIn, address);
    }
}
