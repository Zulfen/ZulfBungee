package com.zulfen.zulfbungee.core.socket.objects.proxy;

import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.core.interfaces.EventCallback;

import java.util.Optional;
import java.util.UUID;

// Separate class, so we can tell this is an event packet
public class ProxyEventPacket extends Packet {

    private transient EventCallback eventCallback;
    private final UUID eventId = UUID.randomUUID();

    // supports callbacks if needed
    public ProxyEventPacket(PacketTypes eventType, EventCallback callbackIn) {
        super(eventType, false, true);
        this.eventCallback = callbackIn;
    }

    public ProxyEventPacket(PacketTypes eventType, Object dataIn) {
        super(eventType, false, true, dataIn);
    }

    public boolean processCallback() {

        if (eventCallback != null) {
            Optional<?> objectOptional = eventCallback.getData();
            if (objectOptional.isPresent()) {
                data[0] = objectOptional.get();
                return true;
            } else {
                return false;
            }
        }

        return data[0] != null;

    }

    public UUID getEventId() {
        return eventId;
    }

}
