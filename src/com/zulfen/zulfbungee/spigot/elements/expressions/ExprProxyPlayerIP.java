package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.net.InetSocketAddress;
import java.util.Optional;

public class ExprProxyPlayerIP extends SimplePropertyExpression<ClientPlayer, String> {

    static {
        register(ExprProxyPlayerIP.class, String.class,"IP", "proxyplayers");
    }

    @Override
    protected String getPropertyName() {
        return "IP Address";
    }

    @Override
    public String convert(ClientPlayer clientPlayer) {
        Optional<InetSocketAddress> optionalIP = clientPlayer.getAddress();
        if (optionalIP.isPresent()) {
            return optionalIP.get().getAddress().toString();
        } else {
            Optional<Packet> send = ZulfBungeeSpigot.getPlugin().getConnectionManager().send(new Packet(PacketTypes.PROXY_PLAYER_IP, true, true,
                    clientPlayer));
            if (send.isPresent()) {
                Object[] dataArray = send.get().getDataArray();
                if (dataArray.length > 0) {
                    return (String) dataArray[0];
                }
            }
        }
        return null;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
