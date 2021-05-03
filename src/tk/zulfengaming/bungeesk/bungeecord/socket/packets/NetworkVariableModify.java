package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;
import tk.zulfengaming.bungeesk.universal.utilclasses.skript.NetworkVariable;
import tk.zulfengaming.bungeesk.universal.utilclasses.skript.SkriptChangeMode;

import java.net.SocketAddress;
import java.util.UUID;

public class NetworkVariableModify extends PacketHandler {

    public NetworkVariableModify(Server serverIn) {
        super(serverIn, false, PacketTypes.NETWORK_VARIABLE_MODIFY);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        NetworkVariable variable = (NetworkVariable) packetIn.getDataSingle();

        StorageImpl storage = getMainServer().getStorage();

        if (variable.isDataValid()) {

            if (variable.getChangeMode().isPresent()) {

                getMainServer().getPluginInstance().getTaskManager().newTask(() -> {

                    SkriptChangeMode mode = variable.getChangeMode().get();

                    if (mode.equals(SkriptChangeMode.SET)) {

                        storage.setVariables(variable);

                    } else if (mode.equals(SkriptChangeMode.DELETE)) {

                        storage.deleteVariables(variable.getName());
                    }
                }, "StorageOperation@" + UUID.randomUUID());
            }
        }

        return null;
    }
}