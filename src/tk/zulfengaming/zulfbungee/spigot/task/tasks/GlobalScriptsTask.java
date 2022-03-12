package tk.zulfengaming.zulfbungee.spigot.task.tasks;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.ClientUpdate;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class GlobalScriptsTask implements Runnable {

    private final ClientConnection connection;

    private final Queue<byte[]> dataQueue = new LinkedList<>();

    private final ArrayList<String> scriptFilesProcessed = new ArrayList<>();

    public GlobalScriptsTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        if (connection.getClientUpdate().isPresent()) {

            ClientUpdate clientUpdate = connection.getClientUpdate().get();

            int currentScriptIndex = 0;

            String[] scriptNames = clientUpdate.getScriptNames();
            Long[] scriptSizes = clientUpdate.getScriptSizes();

            while (currentScriptIndex < scriptNames.length) {

                String scriptName = scriptNames[currentScriptIndex];

                File scriptFile = new File(Skript.getInstance().getDataFolder() + File.separator + "scripts",
                        scriptName);

                if (!scriptFile.exists()) {
                    scriptFilesProcessed.add(scriptName);

                } else {

                    boolean deleted = scriptFile.delete();

                    if (deleted) {
                        scriptFilesProcessed.add(scriptName);
                    }

                }

                connection.send_direct(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, scriptName));

                try {


                    if (scriptFile.createNewFile()) {

                        int bytesRead = 0;
                        long scriptSize = scriptSizes[currentScriptIndex];

                        FileOutputStream outputStream = new FileOutputStream(scriptFile);

                        while (bytesRead < scriptSize) {

                            byte[] data = dataQueue.poll();

                            if (data != null) {
                                outputStream.write(data);
                                bytesRead += data.length;

                            }
                        }

                        if (bytesRead >= scriptSize) {

                            connection.getPluginInstance().logDebug("Script " + scriptName + " processed!");
                            currentScriptIndex += 1;
                            outputStream.close();

                        } else {
                            connection.getPluginInstance().logDebug("Not enough bytes read?");
                        }

                    }


                } catch (IOException e) {
                    connection.getPluginInstance().error("There was an error while processing a global script!");
                    e.printStackTrace();
                }

            }

            for (String name : scriptFilesProcessed) {
                Bukkit.getScheduler().runTask(Skript.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sk reload " + name));
            }

        }

    }

    public Queue<byte[]> getDataQueue() {
        return dataQueue;
    }

}



