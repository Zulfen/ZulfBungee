package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ProxyServer implements Serializable {

    private final String name;
    private final ArrayList<ProxyPlayer> players = new ArrayList<>();

    public ProxyServer(String nameIn) {
        this.name = nameIn;
    }

    public ProxyServer(String nameIn, ProxyPlayer[] playersIn) {
        this.name = nameIn;

        Collections.addAll(players, playersIn);
    }

    public synchronized void addPlayer(ProxyPlayer playerIn) {
        players.add(playerIn);
    }

    public synchronized void removePlayer(ProxyPlayer playerIn) {
        players.remove(playerIn);
    }

    public ArrayList<ProxyPlayer> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }
}
