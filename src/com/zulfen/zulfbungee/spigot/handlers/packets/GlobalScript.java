package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;

public class GlobalScript extends PacketHandler {

    public GlobalScript(Connection<?> connectionIn) {
        super(connectionIn, false, PacketTypes.GLOBAL_SCRIPT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ScriptInfo scriptInfo = (ScriptInfo) packetIn.getDataSingle();
        getConnection().getPluginInstance().getConnectionManager().processGlobalScript(scriptInfo);

    }
}
