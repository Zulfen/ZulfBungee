package com.zulfen.zulfbungee.spigot.handlers.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import com.zulfen.zulfbungee.spigot.interfaces.transport.ClientChannelCommHandler;

import java.util.List;

public class ChannelPayload extends PacketAdapter {

    private final ClientChannelCommHandler channelCommHandler;
    private final MinecraftVersion minecraftVersion;

    public ChannelPayload(ClientChannelCommHandler channelCommHandlerIn, MinecraftVersion versionIn) {
        super(channelCommHandlerIn.getPluginInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD);
        this.channelCommHandler = channelCommHandlerIn;
        this.minecraftVersion = versionIn;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD) {

            PacketContainer packet = event.getPacket();

            String channel;
            // Channel identifiers changed in 1.13 (arrrrgghh)
            if (minecraftVersion.isAtLeast(MinecraftVersion.AQUATIC_UPDATE)) {
                List<MinecraftKey> minecraftKeys = packet.getMinecraftKeys().getValues();
                channel = minecraftKeys.get(0).getFullKey();
            } else {
                channel = packet.getStrings().read(0);
            }

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
