package com.zulfen.zulfbungee.spigot.util;

import ch.njol.skript.registrations.Classes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.ArrayList;
import java.util.List;

public class SkriptVariableUtil {

    public static Object[] toData(NetworkVariable networkVariableIn) {

        Value[] valueArray = networkVariableIn.getValueArray();

        if (valueArray.length > 0) {
            List<Object> list = new ArrayList<>();
            for (Value value : networkVariableIn.getValueArray()) {
                Object deserialize = Classes.deserialize(value.type, value.data);
                list.add(deserialize);
            }
            return list.toArray(new Object[0]);
        }

        return null;

    }

}
