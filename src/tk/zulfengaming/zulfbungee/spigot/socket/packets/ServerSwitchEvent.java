package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

public class ServerSwitchEvent extends PacketHandler {

    public ServerSwitchEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.SERVER_SWITCH_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();
        ClientServer fromServer = (ClientServer) dataContainer.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerSwitchServer(fromServer, dataContainer.getPlayers()[0])
        );

    }
}
