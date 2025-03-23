package com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_20_2;

import com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_13.V13Impl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.entity.Player;

public class V202Impl extends V13Impl {

    @Override
    public Object preparePayload(Player player, byte[] payload) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(payload);
        DiscardedPayload discardedPayload = new DiscardedPayload(ResourceLocation.parse("zproxy:channel"), byteBuf);
        return new ClientboundCustomPayloadPacket(discardedPayload);
    }



}
