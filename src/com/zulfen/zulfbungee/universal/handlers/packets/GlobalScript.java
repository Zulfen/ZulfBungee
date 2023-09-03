package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.universal.config.ProxyConfig;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;

import java.nio.file.Path;
import java.util.List;

public class GlobalScript<P, T> extends PacketHandler<P, T> {

    public GlobalScript(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    // used to retrieve all scripts on the proxy, client will never ask for scripts on its own apart from this, server sends it
    // when needed to the client
    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ProxyConfig<P, T> config = getMainServer().getImpl().getConfig();

        if (config.getBoolean("global-scripts")) {

            List<Path> scriptPaths = config.getScriptPaths();
            int listLength = scriptPaths.size();
            for (int i = 0; i < listLength; i++) {

                Path currentScriptPath = scriptPaths.get(i);

                boolean isLastScript = i == listLength - 1;
                String scriptName = currentScriptPath.getFileName().toString();
                if (!scriptName.startsWith("-")) {
                    connection.sendScript(scriptName, currentScriptPath, ScriptAction.RELOAD, null, isLastScript);
                    config.registerScript(scriptName);
                }

            }

        }

        return null;

    }

}
