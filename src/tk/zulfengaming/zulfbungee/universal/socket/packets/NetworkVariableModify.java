package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.SkriptChangeMode;

import java.util.Optional;

public class NetworkVariableModify<P> extends PacketHandler<P> {

    public NetworkVariableModify(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        NetworkVariable variable = (NetworkVariable) packetIn.getDataSingle();

        Optional<StorageImpl<P>> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl<P> storage = getStorage.get();

            if (variable.getChangeMode().isPresent()) {

                SkriptChangeMode mode = variable.getChangeMode().get();

                switch (mode) {

                    case SET:
                        storage.setVariable(variable);
                        break;
                    case DELETE:
                        storage.deleteVariable(variable.getName());
                        break;
                    case ADD:
                        storage.addToVariable(variable.getName(), variable.getValueArray());
                        break;
                    case REMOVE:
                        storage.removeFromVariable(variable.getName(), variable.getValueArray());
                        break;

                }

            }

        }

        return null;

    }
}