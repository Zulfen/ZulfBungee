package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.core.config.ProxyConfig;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

import java.nio.file.Path;
import java.util.List;

public class GlobalScript<P, T, C> extends PacketHandler<P, T, C> {

    public GlobalScript(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    // used to retrieve all scripts on the proxy, client will never ask for scripts on its own apart from this, server sends it
    // when needed to the client
    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        ProxyConfig<P, T, C> config = getMainServer().getImpl().getConfig();

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
