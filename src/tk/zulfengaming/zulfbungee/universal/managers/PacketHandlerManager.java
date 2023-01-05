package tk.zulfengaming.zulfbungee.universal.managers;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.packets.*;

import java.util.ArrayList;
import java.util.Optional;

public class PacketHandlerManager<P> {

    private final ArrayList<PacketHandler<P>> handlers = new ArrayList<>();

    public PacketHandlerManager(MainServer<P> mainServerIn) {
        addHandler(new Heartbeat<>(mainServerIn));
        addHandler(new ProxyPlayers<>(mainServerIn));
        addHandler(new PlayerSendMessage<>(mainServerIn));
        addHandler(new PlayerSwitchServer<>(mainServerIn));
        addHandler(new ProxyClientInfo<>(mainServerIn));
        addHandler(new NetworkVariableModify<>(mainServerIn));
        addHandler(new NetworkVariableGet<>(mainServerIn));
        addHandler(new ServerSendMessage<>(mainServerIn));
        addHandler(new PlayerServer<>(mainServerIn));
        addHandler(new ProxyPlayerUUID<>(mainServerIn));
        addHandler(new ProxyPlayerOnline<>(mainServerIn));
        addHandler(new GlobalScript<>(mainServerIn));
    }

    public void addHandler(PacketHandler<P> handlerIn) {
        handlers.add(handlerIn);
    }

    public Optional<PacketHandler<P>> getHandler(Packet packetIn) {

        for (PacketHandler<P> handler : handlers) {
            for (PacketTypes type : handler.getTypes()) {
                if (packetIn.getType() == type) {
                    return Optional.of(handler);
                }
            }
        }

        return Optional.empty();

    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {
        if (getHandler(packetIn).isPresent()) {
            return getHandler(packetIn).get().handlePacket(packetIn, connection);
        } else {
            throw new RuntimeException(String.format("Could not find handler for packet type %s", packetIn.getType()));
        }
    }
}
