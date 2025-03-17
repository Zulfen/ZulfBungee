package com.zulfen.zulfbungee.spigot.elements;

import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

public class SkriptComparators {
    static {
        Comparators.registerComparator(ClientServer.class, String.class, (clientServer, string) -> Relation.get(clientServer.name().equals(string)));
        Comparators.registerComparator(ClientPlayer.class, String.class, (clientPlayer, string) -> Relation.get(clientPlayer.getName().equals(string)));
    }
}
