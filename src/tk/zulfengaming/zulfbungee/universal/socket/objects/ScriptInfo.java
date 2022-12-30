package tk.zulfengaming.zulfbungee.universal.socket.objects;

import java.io.Serializable;

public class ScriptInfo implements Serializable {

    private final ScriptAction scriptAction;

    private final String scriptName;

    private final ProxyPlayer sender;

    private final byte[] scriptData;

    public ScriptInfo(ScriptAction scriptActionIn, String scriptNameIn, ProxyPlayer proxyPlayerIn, byte[] dataIn) {
        this.scriptAction = scriptActionIn;
        this.scriptName = scriptNameIn;
        this.sender = proxyPlayerIn;
        this.scriptData = dataIn;
    }

    public byte[] getScriptData() {
        return scriptData;
    }

    public ScriptAction getScriptAction() {
        return scriptAction;
    }

    public ProxyPlayer getSender() {
        return sender;
    }

    public String getScriptName() {
        return scriptName;
    }
}
