package tk.zulfengaming.zulfbungee.bungeecord.task.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class CheckUpdateTask implements Runnable {

    private final ZulfBungeecord pluginInstance;

    private boolean isUpToDate = true;
    private String downloadURL = "";
    private String latestVersion = "";

    public CheckUpdateTask(ZulfBungeecord instanceIn) {
        this.pluginInstance = instanceIn;
    }

    @Override
    public void run() {

        try (InputStream inputStream = new URL("https://api.github.com/repos/Zulfen/ZulfBungee/releases/latest").openStream(); Scanner scanner = new Scanner(inputStream)) {

            StringBuilder builder = new StringBuilder();

            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }

            JsonObject jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();

            latestVersion = jsonObject.get("tag_name").getAsString();
            downloadURL = jsonObject.getAsJsonArray("assets").get(0)
                    .getAsJsonObject().get("browser_download_url").getAsString();

            int[] latestIntVersion = new int[3];
            String[] latestVersionSplit = latestVersion.split("\\.");

            for (int i = 0; i < latestIntVersion.length; i++) {
                latestIntVersion[i] = Integer.parseInt(latestVersionSplit[i]);
            }

            for (int i = 0; i < latestIntVersion.length; i++) {
                if (latestIntVersion[i] > pluginInstance.getVersion()[i]) {
                    isUpToDate = false;
                    break;
                }
            }

            if (isUpToDate) {

                pluginInstance.logInfo(ChatColor.AQUA + "Plugin is up to date! Current version: " + ChatColor.YELLOW + ChatColor.UNDERLINE + pluginInstance.getDescription().getVersion());

            }

        } catch (IOException e) {
            pluginInstance.error("There was an error trying to check for updates!");
            pluginInstance.error("Please notify the project maintainers:");
            e.printStackTrace();
        }

    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpToDate() {
        return isUpToDate;
    }
}
