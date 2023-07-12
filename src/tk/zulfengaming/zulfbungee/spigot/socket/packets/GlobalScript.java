package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;

public class GlobalScript extends PacketHandler {

    public GlobalScript(Connection connectionIn) {
        super(connectionIn, false, PacketTypes.GLOBAL_SCRIPT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ScriptInfo scriptInfo = (ScriptInfo) packetIn.getDataSingle();
        getConnection().getConnectionManager().processGlobalScript(scriptInfo);

    }
}
