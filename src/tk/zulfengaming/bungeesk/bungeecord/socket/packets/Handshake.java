package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import org.json.simple.JSONObject;
import tk.zulfengaming.bungeesk.bungeecord.socket.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class Handshake extends PacketHandler {

    Server server;

    public Handshake(Server serverIn) {
        super(PacketTypes.HANDSHAKE);
        this.server = serverIn;

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        JSONObject data = new JSONObject();

        data.put("accepted", new Boolean(true));

        return new Packet(address, packetIn.name, PacketTypes.HANDSHAKE, data, false);

    }
}
