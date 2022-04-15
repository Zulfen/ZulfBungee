package tk.zulfengaming.zulfbungee.universal.socket;

import java.io.Serializable;

public class ClientUpdateData implements Serializable {

    private final String givenName;
    private final String[] scriptNames;

    public ClientUpdateData(String givenName, String[] scriptNames) {
        this.givenName = givenName;
        this.scriptNames = scriptNames;
    }

    public String[] getScriptNames() {
        return scriptNames;
    }

    public String getGivenName() {
        return givenName;
    }
}
