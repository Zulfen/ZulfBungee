package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;

import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

// Separate class, so we can tell this is an event packet
public class EventPacket extends Packet {

    public EventPacket(PacketTypes eventType, Object dataIn) {
        super(eventType, false, true, dataIn);
    }

}
