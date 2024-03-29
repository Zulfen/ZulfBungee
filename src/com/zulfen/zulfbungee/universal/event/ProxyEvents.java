package com.zulfen.zulfbungee.universal.event;

import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.managers.transport.ChannelMainServer;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.EventPacket;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.util.Optional;
import java.util.UUID;

public class ProxyEvents<P, T> {

    protected final MainServer<P, T> mainServer;

    public ProxyEvents(MainServer<P, T> mainServerIn) {
        this.mainServer = mainServerIn;
    }

    private Optional<ClientServer> toClientServer(String nameIn) {
        Optional<ClientInfo> infoOptional = mainServer.getClientInfo(nameIn);
        return infoOptional.map(info -> new ClientServer(nameIn, info));
    }

    protected void serverConnected(ZulfProxyPlayer<P, T> proxyPlayerIn) {

        mainServer.sendDirectToAllAsync(new EventPacket(PacketTypes.CONNECT_EVENT, () -> mainServer.toClientPlayer(proxyPlayerIn)));

        if (proxyPlayerIn.hasPermission("zulfen.admin")) {
            mainServer.getCheckUpdateTask().checkUpdate(proxyPlayerIn, false);
        }

        mainServer.getConnection(proxyPlayerIn);


    }

    protected void switchServer(String toServerName, String fromServerName, String nameIn, UUID uuidIn) {

        Optional<ClientServer> transferTo = toClientServer(toServerName);
        Optional<ClientServer> transferFrom = toClientServer(fromServerName);

        if (transferFrom.isPresent() && transferTo.isPresent()) {
            mainServer.sendDirectToAllAsync(new EventPacket(PacketTypes.SERVER_SWITCH_EVENT,
                    new ClientPlayerDataContainer(transferFrom.get(), new ClientPlayer(nameIn, uuidIn, transferTo.get()))));
        }

    }

    protected void serverKick(String nameIn, UUID uuidIn, String reason) {
        mainServer.sendDirectToAllAsync(new EventPacket(PacketTypes.KICK_EVENT,
                new ClientPlayerDataContainer(reason, new ClientPlayer(nameIn, uuidIn))));
    }

    protected void serverDisconnect(String nameIn, UUID uuidIn, String previousServerName) {
        Optional<ClientServer> serverOptional = toClientServer(previousServerName);
        serverOptional.ifPresent(clientServer -> mainServer.sendDirectToAllAsync(new EventPacket(PacketTypes.DISCONNECT_EVENT,
                new ClientPlayerDataContainer(clientServer, new ClientPlayer(nameIn, uuidIn)))));
    }

    protected synchronized void pluginMessage(String serverNameIn, byte[] dataIn) {

        Optional<ZulfProxyServer<P, T>> serverOptional = mainServer.getImpl().getServer(serverNameIn);

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

