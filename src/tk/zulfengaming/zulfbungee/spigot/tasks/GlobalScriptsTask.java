package tk.zulfengaming.zulfbungee.spigot.tasks;

import ch.njol.skript.Skript;
import org.bukkit.command.CommandSender;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;

public class GlobalScriptsTask implements Supplier<File> {

    private final ZulfBungeeSpigot pluginInstance;

    private final byte[] data;

    private final String scriptName;
    private final ScriptAction scriptAction;
    private final CommandSender sender;

    public GlobalScriptsTask(ZulfBungeeSpigot pluginInstanceIn, String scriptNameIn, ScriptAction scriptActionIn, CommandSender senderIn, byte[] dataIn) {
        this.pluginInstance = pluginInstanceIn;
        this.scriptName = scriptNameIn;
        this.scriptAction = scriptActionIn;
        this.sender = senderIn;
        this.data = dataIn;
    }


    @Override
    public File get() {

        Thread.currentThread().setName("GlobalScriptsTask");

        File scriptFile = new File(String.format("%s%sscripts", Skript.getInstance().getDataFolder(), File.separator),
                    scriptName);

        switch (scriptAction) {

            case NEW:
                newScript(scriptFile);
                skriptProcess("load");
                break;
            case DELETE:
                removeScript(scriptFile);
                skriptProcess("unload");
                break;
            case RELOAD:
                removeScript(scriptFile);
                newScript(scriptFile);
                skriptProcess("reload");
                break;

        }

        return scriptFile;

    }

    private void newScript(File fileInstance) {

        try {

            boolean created = fileInstance.createNewFile();

            if (created) {

                Files.write(fileInstance.toPath(), data);

            }

        } catch (IOException e) {
            pluginInstance.error(String.format("There was an error trying to save script %s:", fileInstance.getName()));
            e.printStackTrace();
        }

    }

    private void removeScript(File fileInstance) {

        if (fileInstance.exists()) {
            if (!fileInstance.delete()) {
                pluginInstance.warning(String.format("Script file %s could not be deleted.", fileInstance.getName()));
            }
        }

    }

    private void skriptProcess(String commandAction) {
        pluginInstance.getTaskManager().newPluginTask(Skript.getInstance(), () -> pluginInstance.getServer().dispatchCommand(sender, String.format("sk %s %s",
                commandAction, scriptName)));
    }

}



