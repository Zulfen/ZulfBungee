package com.zulfen.zulfbungee.spigot.handlers.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.zulfen.zulfbungee.spigot.handlers.protocol.util.UnsafeNetworkAccess;
import io.netty.buffer.ByteBuf;
import com.zulfen.zulfbungee.spigot.interfaces.transport.ClientChannelCommHandler;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.List;

public class ChannelPayload extends PacketAdapter {

    private final ClientChannelCommHandler channelCommHandler;
    private final MinecraftVersion minecraftVersion;

    private final UnsafeNetworkAccess unsafeNetworkAccess;

    public ChannelPayload(ClientChannelCommHandler channelCommHandlerIn, ProtocolManager protocolManagerIn) {
        super(channelCommHandlerIn.getPluginInstance(), ListenerPriority.HIGHEST, List.of(PacketType.Play.Client.CUSTOM_PAYLOAD), ListenerOptions.ASYNC);
        this.channelCommHandler = channelCommHandlerIn;
        this.minecraftVersion = protocolManagerIn.getMinecraftVersion();
        this.unsafeNetworkAccess = new UnsafeNetworkAccess(minecraftVersion);
        protocolManagerIn.getAsynchronousManager().registerAsyncHandler(this).start();
    }

    private byte[] byteBufToBytes(ByteBuf byteBuf) {
        byte[] message = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), message);
        return message;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {

        if (event.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD) {

            PacketContainer packet = event.getPacket();
            String channel;
            byte[] bytes;

            // Channel identifiers changed in 1.13 (arrrrgghh)
            if (minecraftVersion.isAtLeast(MinecraftVersion.CONFIG_PHASE_PROTOCOL_UPDATE)) {

                // https://vitri-mappings.pyke.io/1.21.4/net/minecraft/network/protocol/common/custom/CustomPacketPayload.html
                Object handle = packet.getHandle();
                Class<?> underlyingMinecraftClass = handle.getClass();

                try {

                    Object payload = underlyingMinecraftClass.getMethod("payload").invoke(handle);
                    Field dataField = payload.getClass().getDeclaredField("data");
                    dataField.setAccessible(true);

                    // on newer builds of paper 1.21 this is actually a byte array instead of a bytebuf
                    if (minecraftVersion.isAtLeast(MinecraftVersion.v1_21_0)) {
                        bytes = (byte[]) dataField.get(payload);
                    } else {
                        ByteBuf buffer = (ByteBuf) dataField.get(payload);
                        bytes = byteBufToBytes(buffer);
                    }

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
                ByteBuf byteBuf = (ByteBuf) packet.getModifier().withType(ByteBuf.class).read(0);
                bytes = byteBufToBytes(byteBuf);
            } else {
                channel = packet.getStrings().read(0);
                ByteBuf byteBuf = (ByteBuf) packet.getModifier().withType(ByteBuf.class).read(0);
                bytes = byteBufToBytes(byteBuf);
            }

            if (channel.equals("zproxy:channel")) {
                channelCommHandler.provideBytes(bytes);
            }

        }
    }

    public void sendCustomPayload(Player player, byte[] payload) {
        if (!unsafeNetworkAccess.sendPayloadPacket(player, payload)) channelCommHandler.getPluginInstance().logDebug("Latest packet was dropped!");
    }


    @Override
    public void onPacketSending(PacketEvent event) {}

}
