package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.nio.file.Path;
import java.util.Map;

public class GlobalScript<P> extends PacketHandler<P> {

    public GlobalScript(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    // used to retrieve all scripts on the proxy, client will never ask for scripts on its own apart from this, server sends it
    // when needed to the client
    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ProxyConfig<P> config = getMainServer().getPluginInstance().getConfig();

        if (config.getBoolean("global-scripts")) {

            getProxy().getTaskManager().newTask(() -> {

                for (Map.Entry<String, Path> script : config.getScriptPaths().entrySet()) {
                    if (!script.getKey().startsWith("-")) {
                        connection.sendScript(script.getKey(), script.getValue(), ScriptAction.RELOAD, null);
                        config.registerScript(script.getKey());
                    }
                }

            });

        }

        return null;

    }

}
