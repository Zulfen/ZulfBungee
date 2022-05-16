package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.spigot.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class GlobalScriptData extends PacketHandler {

    public GlobalScriptData(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.GLOBAL_SCRIPT_DATA);
    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        if (getConnection().getGlobalScriptHandler().isPresent()) {

            GlobalScriptsTask globalScriptsTask = getConnection().getGlobalScriptHandler().get();

            try {

               globalScriptsTask.getDataQueue().put(packetIn.getDataArray());

            } catch (InterruptedException ignored) {

                getConnection().getPluginInstance().warning(String.format("The script %s failed to fully process and may be corrupted!",
                        globalScriptsTask.getCurrentScriptName()));

            }
        }


    }
}
