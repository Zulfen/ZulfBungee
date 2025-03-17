package com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_13;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftFields;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.zulfen.zulfbungee.spigot.handlers.protocol.util.UnsafeNetworkHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class V13Impl implements UnsafeNetworkHandler {

    // protocollib below 1.20.2 has abstractions for this packet
    @Override
    public Object preparePayload(Player player, byte[] payload) {
        PacketContainer channelPacket = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        ByteBuf byteBuf = Unpooled.copiedBuffer(payload);
        channelPacket.getMinecraftKeys().write(0, new MinecraftKey("zproxy", "channel"));
        Object packetDataSerializer = MinecraftReflection.getPacketDataSerializer(byteBuf);
        channelPacket.getModifier().withType(ByteBuf.class).write(0, packetDataSerializer);
        return channelPacket.getHandle();
    }

    @Override
    public boolean sendPacket(Player player, Object packet){
        // protocollib injects its own stuff. maybe this is a sign what im doing is utterly stupid...
        // at runtime this will have a private field called delegate which is the actual channel we want to write to
        Channel channel = getChannel(player);
        if (channel != null) {
            ChannelFuture channelFuture = channel.writeAndFlush(packet, channel.newPromise());
            return channelFuture.awaitUninterruptibly(1, TimeUnit.SECONDS);
        }
        return false;
    }

    private Channel getChannel(Player player) {
        try {
            Object networkManager = MinecraftFields.getNetworkManager(player);
            for (Field networkField : networkManager.getClass().getDeclaredFields()) {
                if (networkField.getType().equals(Channel.class)) {
                    networkField.setAccessible(true);
                    Object protocolLibChannel = networkField.get(networkManager);
                    // protocolib replaces the channel class with its own injected one, bypassing it here
                    Field delegateField = protocolLibChannel.getClass().getDeclaredField("delegate");
                    delegateField.setAccessible(true);
                    return (Channel) delegateField.get(protocolLibChannel);
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }


}
