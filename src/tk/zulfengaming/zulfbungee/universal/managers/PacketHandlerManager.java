package tk.zulfengaming.zulfbungee.universal.managers;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.packets.*;

import java.util.ArrayList;

public class PacketHandlerManager {

    private final ArrayList<PacketHandler> handlers = new ArrayList<>();

    public PacketHandlerManager(MainServer mainServerIn) {
        addHandler(new Heartbeat(mainServerIn));
        addHandler(new ProxyPlayers(mainServerIn));
        addHandler(new PlayerSendMessage(mainServerIn));
        addHandler(new PlayerSwitchServer(mainServerIn));
        addHandler(new ProxyClientInfo(mainServerIn));
        addHandler(new NetworkVariableModify(mainServerIn));
        addHandler(new NetworkVariableGet(mainServerIn));
        addHandler(new ServerSendMessage(mainServerIn));
        addHandler(new PlayerServer(mainServerIn));
        addHandler(new ProxyPlayerUUID(mainServerIn));
        addHandler(new ProxyPlayerOnline(mainServerIn));
        addHandler(new GlobalScript(mainServerIn));
    }

    public void addHandler(PacketHandler handlerIn) {
        handlers.add(handlerIn);
    }

    public PacketHandler getHandler(Packet packetIn) {

        for (PacketHandler handler : handlers) {
            for (PacketTypes type : handler.getTypes()) {
                if (packetIn.getType() == type) {
                    return handler;
                }
            }
        }

        return null;

    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {
        return getHandler(packetIn).handlePacket(packetIn, connection);
    }
}
