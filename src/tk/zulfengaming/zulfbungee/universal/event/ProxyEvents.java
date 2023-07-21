package tk.zulfengaming.zulfbungee.universal.event;

import tk.zulfengaming.zulfbungee.universal.socket.ChannelMainServer;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Optional;
import java.util.UUID;

public class ProxyEvents<P, T> {

    protected final MainServer<P, T> mainServer;

    public ProxyEvents(MainServer<P, T> mainServerIn) {
        this.mainServer = mainServerIn;
    }

    protected void serverConnected(ClientServer toServer, ZulfProxyPlayer<P, T> proxyPlayerIn) {

        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                new ClientPlayer(proxyPlayerIn.getName(), proxyPlayerIn.getUuid(), toServer)));

        if (proxyPlayerIn.hasPermission("zulfen.admin")) {
            mainServer.getPluginInstance().getUpdater().checkUpdate(proxyPlayerIn, false);
        }

    }

    protected void switchServer(ClientServer toServer, String nameIn, UUID uuidIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true,
                new ClientPlayer(nameIn, uuidIn, toServer)));
    }

    protected void serverKick(ClientPlayer playerIn, String reason) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.KICK_EVENT, false, true,
                new ClientPlayerDataContainer(reason, playerIn)));

    }

    protected void serverDisconnect(ClientPlayer playerIn) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                playerIn));
    }

    protected synchronized void pluginMessage(String serverNameIn, byte[] dataIn) {

        Optional<ZulfProxyServer<P, T>> serverOptional = mainServer.getPluginInstance().getServer(serverNameIn);

        if (serverOptional.isPresent()) {

            ZulfProxyServer<P, T> serverIn = serverOptional.get();

            if (mainServer instanceof ChannelMainServer) {

                ChannelMainServer<P, T> channelMainServer = (ChannelMainServer<P, T>) mainServer;

                if (!channelMainServer.isChannelConnectionActive(serverNameIn)) {
                    channelMainServer.acceptMessagingConnection(serverIn.getSocketAddress(), serverNameIn,
                            dataOut -> serverIn.sendData("zproxy:channel", dataOut));

                }

                channelMainServer.proccessPluginMessage(serverNameIn, dataIn);

            }

        }

    }
}

