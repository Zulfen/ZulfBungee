package com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_20_2;

import com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_13.V13Impl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class V202Impl extends V13Impl {

    @Override
    public Object preparePayload(Player player, byte[] payload) {
        Object wrappedBytes = convertBytes(payload);
        Optional<DiscardedPayload> discardedPayload = craftDiscardedPayload(wrappedBytes);
        return discardedPayload.map(ClientboundCustomPayloadPacket::new).orElse(null);
    }

    // this changes in recent builds of paper 1.21
    protected Class<?> getPayloadType() {
        return ByteBuf.class;
    }

    protected Object convertBytes(byte[] bytes) {
        return Unpooled.wrappedBuffer(bytes);
    }

    protected Optional<DiscardedPayload> craftDiscardedPayload(Object payload) {
        try {
            Class<?> discardedPayloadClass = Class.forName("net.minecraft.network.protocol.common.custom.DiscardedPayload");
            Constructor<?> discardedPayloadConstructor = discardedPayloadClass.getConstructor(ResourceLocation.class, getPayloadType());
            DiscardedPayload createdPayload = (DiscardedPayload) discardedPayloadConstructor.newInstance(ResourceLocation.parse("zproxy:channel"), payload);
            return Optional.of(createdPayload);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }



}
