package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.handlers.ClientInfoManager;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;

public class ClientInfo extends PacketHandler {

    public ClientInfo(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.CLIENT_INFO);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyServer serverIn = (ProxyServer) packetIn.getDataSingle();

        ClientInfoManager.addClientInfo(serverIn.getName(), serverIn.getClientInfo());

        return null;

    }
}
