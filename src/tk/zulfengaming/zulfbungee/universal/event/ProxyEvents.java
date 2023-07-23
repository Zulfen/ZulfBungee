package tk.zulfengaming.zulfbungee.universal.event;

import tk.zulfengaming.zulfbungee.universal.managers.transport.ChannelMainServer;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
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

    private Optional<ClientServer> toClientServer(String nameIn) {
        Optional<ClientInfo> infoOptional = mainServer.getClientInfo(nameIn);
        return infoOptional.map(info -> new ClientServer(nameIn, info));
    }

    protected void serverConnected(ZulfProxyPlayer<P, T> proxyPlayerIn) {

        Optional<ClientServer> serverOptional = toClientServer(proxyPlayerIn.getServer().getName());

        if (serverOptional.isPresent()) {

            mainServer.sendDirectToAllAsync(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                    new ClientPlayer(proxyPlayerIn.getName(), proxyPlayerIn.getUuid(), serverOptional.get())));

            if (proxyPlayerIn.hasPermission("zulfen.admin")) {
                mainServer.getPluginInstance().getUpdater().checkUpdate(proxyPlayerIn, false);
            }

        }

    }

    protected void switchServer(String toServerName, String fromServerName, String nameIn, UUID uuidIn) {

        Optional<ClientServer> transferTo = toClientServer(toServerName);
        Optional<ClientServer> transferFrom = toClientServer(fromServerName);

        if (transferFrom.isPresent() && transferTo.isPresent()) {
            mainServer.sendDirectToAllAsync(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true,
                    new ClientPlayerDataContainer(transferFrom.get(), new ClientPlayer(nameIn, uuidIn, transferTo.get()))));
        }

    }

    protected void serverKick(String nameIn, UUID uuidIn, String reason) {
        mainServer.sendDirectToAllAsync(new Packet(PacketTypes.KICK_EVENT, false, true,
                new ClientPlayerDataContainer(reason, new ClientPlayer(nameIn, uuidIn))));
    }

    protected void serverDisconnect(String nameIn, UUID uuidIn, String previousServerName) {
        Optional<ClientServer> serverOptional = toClientServer(previousServerName);
        serverOptional.ifPresent(clientServer -> mainServer.sendDirectToAllAsync(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                new ClientPlayerDataContainer(clientServer, new ClientPlayer(nameIn, uuidIn)))));
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

