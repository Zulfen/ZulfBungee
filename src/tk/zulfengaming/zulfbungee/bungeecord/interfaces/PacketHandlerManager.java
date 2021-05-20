package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import tk.zulfengaming.zulfbungee.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.ArrayList;


public class PacketHandlerManager {

    public final ArrayList<PacketHandler> handlers = new ArrayList<>();

    // TODO: Refactor this!

    public PacketHandlerManager(Server serverIn) {
        handlers.add(new Heartbeat(serverIn));
        handlers.add(new GlobalPlayers(serverIn));
        handlers.add(new PlayerSendMessage(serverIn));
        handlers.add(new ClientHandshake(serverIn));
        handlers.add(new NetworkVariableModify(serverIn));
        handlers.add(new NetworkVariableGet(serverIn));
        handlers.add(new ServerOnline(serverIn));
        handlers.add(new GlobalServers(serverIn));
        handlers.add(new ServerSendMessage(serverIn));
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
