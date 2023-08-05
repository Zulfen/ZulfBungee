package com.zulfen.zulfbungee.spigot.util;

import ch.njol.skript.registrations.Classes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkriptVariableUtil {

    public static Object[] toData(NetworkVariable networkVariableIn) {

        Value[] valueArray = networkVariableIn.getValueArray();

        if (valueArray.length > 0) {
            return Arrays.stream(networkVariableIn.getValueArray())
                    .map(value -> Classes.deserialize(value.type, value.data))
                    .toArray(Object[]::new);
        }

        return null;

    }

}
