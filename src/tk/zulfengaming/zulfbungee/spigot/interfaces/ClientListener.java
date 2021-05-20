package tk.zulfengaming.zulfbungee.spigot.interfaces;

import tk.zulfengaming.zulfbungee.spigot.handlers.ClientListenerManager;

public abstract class ClientListener {

    private final ClientListenerManager clientListenerManager;

    public ClientListener(ClientListenerManager clientListenerManagerIn) {
        clientListenerManager = clientListenerManagerIn;

        clientListenerManagerIn.addListener(this);
    }

    public ClientListenerManager getClientListenerManager() {
        return clientListenerManager;
    }

}
