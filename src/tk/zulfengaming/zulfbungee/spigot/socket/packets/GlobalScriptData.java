package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class GlobalScriptData extends PacketHandler {

    public GlobalScriptData(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.GLOBAL_SCRIPT_DATA);
    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        Object[] packetData = packetIn.getDataArray();
        byte[] scriptData = new byte[packetData.length];

        for(int i = 0; i < packetData.length; i++) scriptData[i] = (byte) packetData[i];

        getConnection().getGlobalScriptManager().getDataQueue().offer(scriptData);

        return null;

    }
}
