package tk.zulfengaming.zulfbungee.universal.event;

import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public class ProxyEvents {

    private final MainServer mainServer;

    public ProxyEvents(MainServer mainServerIn) {
        this.mainServer = mainServerIn;
    }

    protected void serverConnected(ProxyPlayer playerIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                playerIn));
    }

    protected void switchServerEvent(ProxyPlayer playerIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true,
                playerIn));
    }

    protected void serverKick(ProxyPlayer playerIn, String reason) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.KICK_EVENT, false, true,
                new ProxyPlayerDataContainer(reason, playerIn)));

    }

    protected void serverDisconnect(ProxyPlayer playerIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                playerIn));
    }
}

