package tk.zulfengaming.zulfbungee.spigot.tasks;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import org.apache.commons.lang.ArrayUtils;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class GlobalScriptsTask implements Runnable {

    private final ClientConnection connection;

    private final SynchronousQueue<Object[]> dataQueue = new SynchronousQueue<>();

    private final ScriptInfo[] scriptInfos;

    private String currentScriptName = "";

    public GlobalScriptsTask(ClientConnection connectionIn, ScriptInfo[] scriptInfoIn) {
        this.connection = connectionIn;
        this.scriptInfos = scriptInfoIn;
    }

    @Override
    public void run() {

        for (ScriptInfo scriptInfo : scriptInfos) {

            List<String> scriptNames = Arrays.asList(scriptInfo.getScriptNames());

            if (scriptInfo.getScriptAction() == ScriptAction.DELETE || scriptInfo.getScriptAction() == ScriptAction.RELOAD)  {

                for (File scriptFile : connection.getScripts()) {
                    ScriptLoader.unloadScript(scriptFile);
                    //noinspection ResultOfMethodCallIgnored
                    scriptFile.delete();
                }

                connection.getScripts().removeIf(file -> (scriptNames.contains(file.getName())));

            }

            for (String name : scriptInfo.getScriptNames()) {

                currentScriptName = name;

                File scriptFile = new File(Skript.getInstance().getDataFolder() + File.separator + "scripts",
                        name);

                if (scriptInfo.getScriptAction() == ScriptAction.RELOAD || scriptInfo.getScriptAction() == ScriptAction.NEW) {

                    connection.send_direct(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, name));

                    try {

                        boolean created = scriptFile.createNewFile();

                        if (created) {

                            Object[] rawData = dataQueue.take();

                            int dataLen = rawData.length;

                            if (dataLen != 0) {

                                Byte[] primitiveBytesIn = new Byte[dataLen];

                                for (int i = 0; i < dataLen; i++) {
                                    primitiveBytesIn[i] = (Byte) rawData[i];
                                }

                                Files.write(scriptFile.toPath(), ArrayUtils.toPrimitive(primitiveBytesIn));

                            }

                            connection.getScripts().add(scriptFile);

                            Config config = ScriptLoader.loadStructure(scriptFile);
                            ScriptLoader.loadScripts(Collections.singletonList(config));

                        }

                    } catch (InterruptedException ignored) {

                    } catch (IOException e) {
                        connection.getPluginInstance().error(String.format("There was an error trying to save script %s:", scriptFile.getName()));
                        e.printStackTrace();
                    }

                }

            }

        }

    }

    public String getCurrentScriptName() {
        return currentScriptName;
    }

    public SynchronousQueue<Object[]> getDataQueue() {
        return dataQueue;
    }

}



