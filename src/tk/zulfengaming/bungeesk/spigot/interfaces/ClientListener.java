package tk.zulfengaming.bungeesk.spigot.interfaces;

public abstract class ClientListener implements ClientEvents {

    public ClientListener(ClientManager clientManagerIn) {
        clientManagerIn.addListener(this);
    }

}
