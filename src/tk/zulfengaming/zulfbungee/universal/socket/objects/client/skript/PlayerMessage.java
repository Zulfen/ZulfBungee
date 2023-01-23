package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.io.Serializable;

public class PlayerMessage implements Serializable {

    private final ClientServer fromServer;
    private final ClientPlayer[] toPlayers;
    private final String message;

    public PlayerMessage(ClientServer fromServer, ClientPlayer[] toPlayers, String message) {
        this.fromServer = fromServer;
        this.toPlayers = toPlayers;
        this.message = message;
    }

    public ClientServer getFromServer() {
        return fromServer;
    }

    public ClientPlayer[] getToPlayers() {
        return toPlayers;
    }

    public String getMessage() {
        return message;
    }

}
