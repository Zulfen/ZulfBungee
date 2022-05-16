package tk.zulfengaming.zulfbungee.universal.socket;

import java.io.Serializable;

public class ScriptInfo implements Serializable {

    private final ScriptAction scriptAction;

    private final String[] scriptNames;

    public ScriptInfo(ScriptAction scriptActionIn, String[] scriptNamesIn) {
        this.scriptAction = scriptActionIn;
        this.scriptNames = scriptNamesIn;
    }

    public ScriptAction getScriptAction() {
        return scriptAction;
    }

    public String[] getScriptNames() {
        return scriptNames;
    }
}
