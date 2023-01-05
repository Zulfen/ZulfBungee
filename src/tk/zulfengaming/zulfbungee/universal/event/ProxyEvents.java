package tk.zulfengaming.zulfbungee.universal.event;

import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public class ProxyEvents<P> {

    protected final MainServer<P> mainServer;

    public ProxyEvents(MainServer<P> mainServerIn) {
        this.mainServer = mainServerIn;
    }

    protected void serverConnected(ClientServer toServer, ZulfProxyPlayer<P> playerIn) {

        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                new ClientPlayer(playerIn.getName(), playerIn.getUuid(), toServer)));

        if (playerIn.hasPermission("zulfen.admin")) {
            mainServer.getPluginInstance().getUpdater().checkUpdate(playerIn, false);
        }

    }

    protected void switchServer(ClientServer toServer, ZulfProxyPlayer<P> playerIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true,
                new ClientPlayer(playerIn.getName(), playerIn.getUuid(), toServer)));
    }

    protected void serverKick(ClientPlayer playerIn, String reason) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.KICK_EVENT, false, true,
                new ClientPlayerDataContainer(reason, playerIn)));

    }

    protected void serverDisconnect(ClientPlayer playerIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                playerIn));
    }
}

