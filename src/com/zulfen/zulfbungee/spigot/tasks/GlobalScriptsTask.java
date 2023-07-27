package com.zulfen.zulfbungee.spigot.tasks;

import ch.njol.skript.Skript;
import org.bukkit.command.CommandSender;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class GlobalScriptsTask implements Supplier<File> {

    private final ZulfBungeeSpigot pluginInstance;

    private final byte[] data;

    private final String scriptName;
    private final File scriptFile;
    private final File unloadedScriptFile;
    private final ScriptAction scriptAction;
    private final CommandSender sender;

    public GlobalScriptsTask(ZulfBungeeSpigot pluginInstanceIn, String scriptNameIn, ScriptAction scriptActionIn, CommandSender senderIn, byte[] dataIn) {
        this.pluginInstance = pluginInstanceIn;
        this.scriptName = scriptNameIn;
        this.scriptFile = new File(String.format("%s%sscripts", Skript.getInstance().getDataFolder(), File.separator),
                scriptName);
        this.unloadedScriptFile = new File(String.format("%s%sscripts", Skript.getInstance().getDataFolder(), File.separator),
                "-" + scriptName);
        this.scriptAction = scriptActionIn;
        this.sender = senderIn;
        this.data = dataIn;
    }


    @Override
    public File get() {

        Thread.currentThread().setName("GlobalScriptsTask");
        boolean exists = scriptFile.exists();

        switch (scriptAction) {

            // proxy may report this as new to its filesystem, but will already exist on client's filesystem.
            case DELETE:
                if (exists) {
                    skriptProcess("disable");
                    removeScript();
                }
                break;
            case RELOAD:
                reloadScript();
                skriptProcess("reload");
                break;

        }

        return scriptFile;

    }

    private void reloadScript() {
        removeScript();
        newScript();
    }

    private void newScript() {

        try {

            boolean created = scriptFile.createNewFile();

            if (created) {
                Files.write(scriptFile.toPath(), data);
            }

        } catch (IOException e) {
            pluginInstance.error(String.format("There was an error trying to save script %s:", scriptFile.getName()));
            e.printStackTrace();
        }

    }

    private void removeScript() {

        if (unloadedScriptFile.exists()) {
            boolean delete = unloadedScriptFile.delete();
            if (!delete) {
                pluginInstance.warning(String.format("Couldn't delete unloaded script file %s", unloadedScriptFile.getName()));
            }
        }

        if (scriptFile.exists()) {
            boolean delete = scriptFile.delete();
            if (!delete) {
                pluginInstance.warning(String.format("Couldn't delete loaded script file %s", scriptFile.getName()));
            }
        }


    }

    private void skriptProcess(String commandAction) {

        try {
            pluginInstance.getTaskManager().newMainThreadTask(() -> {
                pluginInstance.getServer().dispatchCommand(sender, String.format("sk %s %s",
                        commandAction, scriptName));
                return null;
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            pluginInstance.warning(String.format("Error processing script file %s:", scriptName));
            e.printStackTrace();
        }

    }

}



