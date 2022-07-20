package tk.zulfengaming.zulfbungee.bungeecord.managers;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.util.EnumMap;


public class PacketHandlerManager {

    private final EnumMap<PacketTypes, PacketHandler> handlers = new EnumMap<>(PacketTypes.class);

    public PacketHandlerManager(Server serverIn) {
        addHandler(new Heartbeat(serverIn));
        addHandler(new ProxyPlayers(serverIn));
        addHandler(new PlayerSendMessage(serverIn));
        addHandler(new PlayerSwitchServer(serverIn));
        addHandler(new ProxyClientInfo(serverIn));
        addHandler(new NetworkVariableModify(serverIn));
        addHandler(new NetworkVariableGet(serverIn));
        addHandler(new ServerSendMessage(serverIn));
        addHandler(new PlayerServer(serverIn));
        addHandler(new ProxyPlayerUUID(serverIn));
        addHandler(new ProxyPlayerOnline(serverIn));
        addHandler(new GlobalScript(serverIn));
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
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {
        return getHandler(packetIn).handlePacket(packetIn, connection);
    }
}
