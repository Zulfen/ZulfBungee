package tk.zulfengaming.bungeesk.spigot.interfaces;

import tk.zulfengaming.bungeesk.universal.interfaces.ClientEvents;

public abstract class ClientListener implements ClientEvents {

    public ClientListener(ClientListenerManager clientListenerManagerIn) {
        clientListenerManagerIn.addListener(this);
    }

}
