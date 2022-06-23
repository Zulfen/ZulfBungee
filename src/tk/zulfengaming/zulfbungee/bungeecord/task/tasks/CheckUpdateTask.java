package tk.zulfengaming.zulfbungee.bungeecord.task.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.bungeecord.util.UpdateResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;


public class CheckUpdateTask implements Supplier<Optional<UpdateResult>> {

    private final ZulfBungeecord pluginInstance;

    public CheckUpdateTask(ZulfBungeecord instanceIn) {
        this.pluginInstance = instanceIn;
    }

    @Override
    public Optional<UpdateResult> get() {

        try (InputStream inputStream = new URL("https://api.github.com/repos/Zulfen/ZulfBungee/releases/latest").openStream(); Scanner scanner = new Scanner(inputStream)) {

            StringBuilder builder = new StringBuilder();

            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }

            JsonObject jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();

            String latestVersion = jsonObject.get("tag_name").getAsString();
            String downloadURL = jsonObject.getAsJsonArray("assets").get(0)
                    .getAsJsonObject().get("browser_download_url").getAsString();

            int[] latestIntVersion = new int[3];
            String[] latestVersionSplit = latestVersion.split("v")[1].split("\\.");

            for (int i = 0; i < latestVersionSplit.length; i++) {
                latestIntVersion[i] = Integer.parseInt(latestVersionSplit[i]);
            }

            boolean isUpToDate = true;

            for (int i = 0; i < latestVersionSplit.length; i++) {

                int minorVer = pluginInstance.getVersion()[i];

                if (latestIntVersion[i] > minorVer) {
                    isUpToDate = false;
                    break;
                }

            }

            if (!isUpToDate) {
               return Optional.of(new UpdateResult(latestVersion, downloadURL));
            }

        } catch (IOException e) {
            pluginInstance.error("There was an error trying to check for updates!");
            pluginInstance.error("Please notify the project maintainers:");
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
