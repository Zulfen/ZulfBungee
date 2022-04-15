package tk.zulfengaming.zulfbungee.spigot.task.tasks;

import ch.njol.skript.Skript;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.ClientUpdateData;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

public class GlobalScriptsTask implements Runnable {

    private final ClientConnection connection;

    private final SynchronousQueue<Object[]> dataQueue = new SynchronousQueue<>();

    private final ArrayList<String> scriptFilesProcessed = new ArrayList<>();

    public GlobalScriptsTask(ClientConnection connectionIn) {
        this.connection = connectionIn;
    }

    @Override
    public void run() {

        if (connection.getClientUpdate().isPresent()) {

            connection.getPluginInstance().logDebug("Global Script Task started!");

            ClientUpdateData clientUpdateData = connection.getClientUpdate().get();

            String[] scriptNames = clientUpdateData.getScriptNames();

            for (String name : scriptNames) {

                File scriptFile = new File(Skript.getInstance().getDataFolder() + File.separator + "scripts",
                        name);

                if (!scriptFile.exists()) {

                    scriptFilesProcessed.add(name);

                } else {

                    boolean deleted = scriptFile.delete();

                    if (deleted) {
                        scriptFilesProcessed.add(name);
                    }

                }

                connection.send_direct(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, name));

                try {

                    Object[] rawData = dataQueue.take();

                    int dataLen = rawData.length;

                    if (dataLen != 0) {

                        Byte[] primitiveBytesIn = new Byte[dataLen];

                        for (int i = 0; i < dataLen; i++) {
                            primitiveBytesIn[i] = (Byte) rawData[i];
                        }

                        Files.write(scriptFile.toPath(), ArrayUtils.toPrimitive(primitiveBytesIn));

                    }

                } catch (InterruptedException ignored) {

                } catch (IOException e) {
                    connection.getPluginInstance().error(String.format("There was an error trying to save script %s:", scriptFile.getName()));
                    e.printStackTrace();
                }


            }

            for (String name : scriptFilesProcessed) {
                Bukkit.getScheduler().runTask(Skript.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sk reload " + name));
            }

        }

    }

    public SynchronousQueue<Object[]> getDataQueue() {
        return dataQueue;
    }

}



