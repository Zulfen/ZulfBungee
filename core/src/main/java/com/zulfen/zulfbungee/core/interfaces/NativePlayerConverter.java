package com.zulfen.zulfbungee.core.interfaces;

import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;
import java.util.function.Function;

// TODO: Make my own interface to not use Function
public class NativePlayerConverter<P, T, C> implements Function<T, Optional<ZulfProxyPlayer<P, T, C>>> {
    @Override
    public Optional<ZulfProxyPlayer<P, T, C>> apply(T nativePlayer) {
        return Optional.empty();
    }
}
