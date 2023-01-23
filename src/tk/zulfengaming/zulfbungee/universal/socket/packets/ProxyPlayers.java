package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyPlayers<P> extends PacketHandler<P> {

    public ProxyPlayers(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PROXY_PLAYERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connectionIn) {

        ArrayList<ClientPlayer> playersOut = new ArrayList<>();

        if (packetIn.getDataArray().length != 0) {

            ClientServer[] servers = Stream.of(packetIn.getDataArray())
                    .filter(Objects::nonNull)
                    .filter(ClientServer.class::isInstance)
                    .map(ClientServer.class::cast)
                    .toArray(ClientServer[]::new);

            for (ClientServer server : servers) {

                BaseServerConnection<P> serverConnection = getMainServer().getConnectionFromName(server.getName());

                if (serverConnection != null) {
                    List<ZulfProxyPlayer<P>> players = connectionIn.getPlayers();
                    for (ZulfProxyPlayer<P> player : players) {
                        playersOut.add(new ClientPlayer(player.getName(), player.getUuid()));
                    }
                }

            }


        } else {

            playersOut = getMainServer().getAllPlayers().stream()
                    .map(player -> new ClientPlayer(player.getName(), player.getUuid()))
                    .collect(Collectors.toCollection(ArrayList::new));


        }


        return new Packet(PacketTypes.PROXY_PLAYERS, false, false, playersOut.toArray(new ClientPlayer[0]));

    }
}
