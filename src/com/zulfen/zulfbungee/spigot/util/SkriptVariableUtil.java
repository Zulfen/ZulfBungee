package com.zulfen.zulfbungee.spigot.util;

import ch.njol.skript.registrations.Classes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.Objects;
import java.util.stream.Stream;

public class SkriptVariableUtil {

    public static Object[] toData(NetworkVariable networkVariableIn) {

        Value[] valueArray = networkVariableIn.getValueArray();

        if (valueArray.length > 0) {
            return Stream.of(networkVariableIn.getValueArray())
                    .filter(Objects::nonNull)
                    .map(value -> Classes.deserialize(value.type, value.data))
                    .toArray(Object[]::new);
        }

        return null;

    }

}
