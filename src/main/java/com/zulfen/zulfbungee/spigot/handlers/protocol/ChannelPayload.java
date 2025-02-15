package com.zulfen.zulfbungee.spigot.handlers.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import com.zulfen.zulfbungee.spigot.interfaces.transport.ClientChannelCommHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ChannelPayload extends PacketAdapter {

    private final ClientChannelCommHandler channelCommHandler;
    private final MinecraftVersion minecraftVersion;

    public ChannelPayload(ClientChannelCommHandler channelCommHandlerIn, ProtocolManager protocolManagerIn) {
        super(channelCommHandlerIn.getPluginInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.CUSTOM_PAYLOAD);
        this.channelCommHandler = channelCommHandlerIn;
        this.minecraftVersion = protocolManagerIn.getMinecraftVersion();
        protocolManagerIn.addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD) {

            PacketContainer packet = event.getPacket();

            String channel;
            ByteBuf byteBuffer;

            // Channel identifiers changed in 1.13 (arrrrgghh)
            if (minecraftVersion.isAtLeast(MinecraftVersion.CONFIG_PHASE_PROTOCOL_UPDATE)) {

                // https://vitri-mappings.pyke.io/1.21.4/net/minecraft/network/protocol/common/custom/CustomPacketPayload.html
                Object handle = packet.getHandle();
                Class<?> underlyingMinecraftClass = handle.getClass();

                try {

                    Object payload = underlyingMinecraftClass.getMethod("payload").invoke(handle);
                    Field dataField = payload.getClass().getDeclaredField("data");
                    dataField.setAccessible(true);

                    byteBuffer = (ByteBuf) dataField.get(payload);

                    Method idMethod = payload.getClass().getMethod("id");
                    Object resourceLocation = idMethod.invoke(payload); // this should be the ResourceLocation object

                    Field namespaceField = resourceLocation.getClass().getDeclaredField("namespace");
                    Field pathField = resourceLocation.getClass().getDeclaredField("path");
                    namespaceField.setAccessible(true);
                    pathField.setAccessible(true);

                    String namespaceValue = (String) namespaceField.get(resourceLocation);
                    String pathValue = (String) pathField.get(resourceLocation);

                    channel = namespaceValue + ":" + pathValue;

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                         NoSuchFieldException e) {
                    throw new RuntimeException("Workaround for lack of ProtocolLib update to reflect new channel payload changes failed spectacularly: " + e.getCause().toString());
                }

            } else if (minecraftVersion.isAtLeast(MinecraftVersion.AQUATIC_UPDATE)) {
                List<MinecraftKey> minecraftKeys = packet.getMinecraftKeys().getValues();
                channel = minecraftKeys.getFirst().getFullKey();
                byteBuffer = (ByteBuf) packet.getModifier().withType(ByteBuf.class).read(0);
            } else {
                channel = packet.getStrings().read(0);
                byteBuffer = (ByteBuf) packet.getModifier().withType(ByteBuf.class).read(0);
            }

            if (channel.equals("zproxy:channel")) {
                byte[] message = new byte[byteBuffer.readableBytes()];
                byteBuffer.getBytes(byteBuffer.readerIndex(), message);
                channelCommHandler.provideBytes(message);
            }

        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }

}
