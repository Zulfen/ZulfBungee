package tk.zulfengaming.zulfbungee.universal.socket.packets;


import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class ProxyClientInfo<P> extends PacketHandler<P> {

    public ProxyClientInfo(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientInfo clientInfo = (ClientInfo) packetIn.getDataSingle();
        connection.setClientInfo(clientInfo);

        InetSocketAddress socketAddressIn = (InetSocketAddress) connection.getAddress();

        int portIn = clientInfo.getMinecraftPort();

        for (Map.Entry<String, ZulfServerInfo<P>> info : getProxy().getServersCopy().entrySet()) {

            InetSocketAddress infoSockAddr = (InetSocketAddress) info.getValue().getSocketAddress();

            int infoPort = infoSockAddr.getPort();

            boolean isLocalHost = false;

            try {
                isLocalHost = socketAddressIn.getAddress().equals(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                getMainServer().getPluginInstance().warning("Could not resolve localhost on this machine. Names for connections could break!");
            }

            if ((infoSockAddr.equals(socketAddressIn) || isLocalHost) && portIn == infoPort) {

                String name = info.getKey();
                getMainServer().addActiveConnection(connection, name);

                return new Packet(PacketTypes.CONNECTION_NAME, false, true, name);

            }
        }

        return null;
    }
}