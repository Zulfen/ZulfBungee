package tk.zulfengaming.zulfbungee.spigot.handlers.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.netty.buffer.ByteBuf;
import tk.zulfengaming.zulfbungee.spigot.handlers.transport.ClientChannelCommHandler;

public class ChannelPayload extends PacketAdapter {

    private final ClientChannelCommHandler channelCommHandler;

    public ChannelPayload(ClientChannelCommHandler channelCommHandlerIn) {
        super(channelCommHandlerIn.getPluginInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD);
        this.channelCommHandler = channelCommHandlerIn;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD) {

            PacketContainer packet = event.getPacket();
            String channel = packet.getStrings().read(0);

            if (channel.equals("zproxy:channel")) {
                ByteBuf byteBuf = (ByteBuf) packet.getModifier().withType(ByteBuf.class).read(0);
                byte[] message = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(byteBuf.readerIndex(), message);
                channelCommHandler.provideBytes(message);
            }

        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {}

}
