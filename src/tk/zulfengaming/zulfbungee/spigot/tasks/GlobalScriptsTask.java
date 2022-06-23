package tk.zulfengaming.zulfbungee.spigot.tasks;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.util.OpenCloseable;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.function.Supplier;

public class GlobalScriptsTask implements Supplier<File> {

    private final ClientConnection connection;

    private final byte[] data;

    private final String scriptName;
    private final ScriptAction scriptAction;

    public GlobalScriptsTask(ClientConnection connectionIn, String scriptNameIn, ScriptAction scriptActionIn, byte[] dataIn) {
        this.connection = connectionIn;
        this.scriptName = scriptNameIn;
        this.scriptAction = scriptActionIn;
        this.data = dataIn;
    }


    @Override
    public File get() {

        File scriptFile = new File(Skript.getInstance().getDataFolder() + File.separator + "scripts",
                    scriptName);

        switch (scriptAction) {

            case NEW:
                newScript(scriptFile);
                break;
            case DELETE:
                removeScript(scriptFile);
                break;
            case RELOAD:
                removeScript(scriptFile);
                newScript(scriptFile);
                break;

        }

        return scriptFile;

    }

    private void newScript(File fileInstance) {

        try {

            boolean created = fileInstance.createNewFile();

            if (created) {

                Files.write(fileInstance.toPath(), data);

                Config config = ScriptLoader.loadStructure(fileInstance);
                ScriptLoader.loadScripts(Collections.singletonList(config), OpenCloseable.EMPTY);

            }

        } catch (IOException e) {
            connection.getPluginInstance().error(String.format("There was an error trying to save script %s:", fileInstance.getName()));
            e.printStackTrace();
        }

    }

    private void removeScript(File fileInstance) {

        ScriptLoader.unloadScript(fileInstance);

        if (fileInstance.exists()) {
            if (!fileInstance.delete()) {
                connection.getPluginInstance().warning(String.format("Script file %s could not be deleted.", fileInstance.getName()));
            }
        }

    }
}



