package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.handlers.ClientInfoManager;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;

public class ClientDisconnect extends PacketHandler {

    public ClientDisconnect(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.CLIENT_DISCONNECT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyServer serverIn = (ProxyServer) packetIn.getDataSingle();

        ClientInfoManager.removeClientInfo(serverIn.getName(), serverIn.getClientInfo());

        return null;

    }
}
