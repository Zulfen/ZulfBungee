package com.zulfen.zulfbungee.core.socket.objects.client;

import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;

import java.util.UUID;

public class ProxyEventResponse extends Packet {

    public ProxyEventResponse(UUID proxyId) {
        super(PacketTypes.PROXY_EVENT_PROCESSED, true, true, new Object[]{proxyId});
    }

    public UUID getEventId() {
        return getDataSingle(UUID.class);
    }

}
