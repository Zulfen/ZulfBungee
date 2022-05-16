package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptInfo;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.stream.Stream;

public class GlobalScriptHeader extends PacketHandler {

    public GlobalScriptHeader(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.GLOBAL_SCRIPT_HEADER);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ScriptInfo[] scriptInfos = Stream.of(packetIn.getDataArray())
                .filter(Objects::nonNull)
                .filter(ScriptInfo.class::isInstance)
                .map(ScriptInfo.class::cast)
                .toArray(ScriptInfo[]::new);

        getConnection().requestGlobalScripts(scriptInfos);

    }
}
