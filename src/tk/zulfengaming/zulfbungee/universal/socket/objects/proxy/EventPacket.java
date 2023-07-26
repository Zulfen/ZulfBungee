package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;

import tk.zulfengaming.zulfbungee.universal.interfaces.EventCallback;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.util.Optional;

// Separate class, so we can tell this is an event packet
public class EventPacket extends Packet {

    private transient EventCallback eventCallback;

    // supports callbacks if needed
    public EventPacket(PacketTypes eventType, EventCallback callbackIn) {
        super(eventType, false, true);
        this.eventCallback = callbackIn;
    }

    public EventPacket(PacketTypes eventType, Object dataIn) {
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

}
