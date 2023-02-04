package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

public class GlobalScript<P> extends PacketHandler<P> {

    public GlobalScript(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    // used to retrieve all scripts on the proxy, client will never ask for scripts on its own apart from this, server sends it
    // when needed to the client
    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        if (getMainServer().getPluginInstance().getConfig().getBoolean("global-scripts")) {

            for (String scriptName : getMainServer().getPluginInstance().getConfig().getScripts()) {
                connection.sendScript(getMainServer().getPluginInstance().getConfig().getScriptPath(scriptName), ScriptAction.RELOAD, null);
            }

        }

        return null;

    }

}
