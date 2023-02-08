package tk.zulfengaming.zulfbungee.universal.task.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.handlers.util.UpdateResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public class CheckUpdateTask<P> implements Supplier<Optional<UpdateResult>> {

    private final ZulfBungeeProxy<P> pluginInstance;

    public CheckUpdateTask(ZulfBungeeProxy<P> instanceIn) {
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

            String latestVersionSplit = latestVersion.split("v")[1];

            if (!latestVersionSplit.equals(pluginInstance.getVersion())) {
                return Optional.of(new UpdateResult(latestVersion, downloadURL));
            }

        } catch (IOException e) {
            pluginInstance.error("There was an error trying to check for updates!");
            pluginInstance.error("Please notify the project maintainers:");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void checkUpdate(ProxyCommandSender<P> senderIn, boolean notifySuccess) {

        CompletableFuture.supplyAsync(this)
                .thenAccept(updateResult -> {

                    if (updateResult.isPresent()) {

                        UpdateResult getUpdaterResult = updateResult.get();

                        senderIn.sendMessage(String.format(Constants.MESSAGE_PREFIX + String.format("A new update to ZulfBungee is available! &e(%s)",
                                getUpdaterResult.getLatestVersion())));
                        senderIn.sendMessage(Constants.MESSAGE_PREFIX + "Copy this link into a browser for a direct download:");
                        senderIn.sendMessage(Constants.MESSAGE_PREFIX + String.format("&3&n%s", getUpdaterResult.getDownloadURL()));

                    } else if (notifySuccess) {

                        senderIn.sendMessage(String.format(Constants.MESSAGE_PREFIX + String.format("ZulfBungee is up to date! &e(%s)",
                                pluginInstance.getVersion())));
                    }

                });
    }

}
