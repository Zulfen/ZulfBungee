package tk.zulfengaming.zulfbungee.universal.socket;

import java.io.Serializable;

public class ScriptInfo implements Serializable {

    private final ScriptAction scriptAction;

    private final String scriptName;

    private byte[] scriptData;

    public ScriptInfo(ScriptAction scriptActionIn, String scriptNameIn, byte[] dataIn) {
        this.scriptAction = scriptActionIn;
        this.scriptName = scriptNameIn;
        this.scriptData = dataIn;
    }

    public byte[] getScriptData() {
        return scriptData;
    }

    public ScriptAction getScriptAction() {
        return scriptAction;
    }

    public String getScriptName() {
        return scriptName;
    }
}
