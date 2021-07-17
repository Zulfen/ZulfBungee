package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.EnumMap;


public class PacketHandlerManager {

    private final EnumMap<PacketTypes, PacketHandler> handlers = new EnumMap<>(PacketTypes.class);

    public PacketHandlerManager(Server serverIn) {
        addHandler(new Heartbeat(serverIn));
        addHandler(new ProxyPlayers(serverIn));
        addHandler(new PlayerSendMessage(serverIn));
        addHandler(new ClientHandshake(serverIn));
        addHandler(new NetworkVariableModify(serverIn));
        addHandler(new NetworkVariableGet(serverIn));
        addHandler(new ServerSendMessage(serverIn));
        addHandler(new PlayerServer(serverIn));
        addHandler(new ProxyPlayerOnline(serverIn));
    }

    public void addHandler(PacketHandler handlerIn) {
        for (PacketTypes type : handlerIn.getTypes()) {
            handlers.put(type, handlerIn);
        }
    }

    public PacketHandler getHandler(Packet packetIn) {
        return handlers.get(packetIn.getType());
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        return getHandler(packetIn).handlePacket(packetIn, address);
    }
}
