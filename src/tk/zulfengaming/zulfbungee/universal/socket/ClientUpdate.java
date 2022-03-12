package tk.zulfengaming.zulfbungee.universal.socket;

import java.io.Serializable;

public class ClientUpdate implements Serializable {

    private final String givenName;
    private final Long[] scriptSizes;
    private final String[] scriptNames;

    public ClientUpdate(String givenName, Long[] scriptSizes, String[] scriptNames) {
        this.givenName = givenName;
        this.scriptSizes = scriptSizes;
        this.scriptNames = scriptNames;
    }

    public String[] getScriptNames() {
        return scriptNames;
    }

    public Long[] getScriptSizes() {
        return scriptSizes;
    }

    public String getGivenName() {
        return givenName;
    }
}
