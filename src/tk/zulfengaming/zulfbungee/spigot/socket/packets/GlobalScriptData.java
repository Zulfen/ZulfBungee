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

        // send this to another thread to deal with just in-case it takes ages
        try {
            getConnection().getGlobalScriptManager().getDataQueue().put(packetIn.getDataArray());
        } catch (InterruptedException ignored) {

        }

        return null;

    }
}
