package tk.zulfengaming.zulfbungee.bungeecord.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class YamlConfig {

    private final File configFile;
    private Path scriptsFolderPath;

    private final ArrayList<String> availableScripts = new ArrayList<>();

    private ConfigurationProvider configObject;

    private Configuration loadedConfig;

    @SuppressWarnings("UnstableApiUsage")
    public YamlConfig(ZulfBungeecord instanceIn) {

        this.configFile = new File(instanceIn.getDataFolder(), "config.yml");

        try {

            if (!instanceIn.getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                instanceIn.getDataFolder().mkdir();

            }

            // Thank you https://www.spigotmc.org/members/tux.2180/ <3

            if (!configFile.exists())

                try {

                    boolean created = configFile.createNewFile();

                    if (created) {
                        try (InputStream is = instanceIn.getResourceAsStream("bungeecord.yml");
                             OutputStream os = new FileOutputStream(configFile)) {
                            ByteStreams.copy(is, os);
                        }
                    }

                } catch (IOException e) {
                    instanceIn.error("There was an error copying the default config:");
                    e.printStackTrace();
                }

            this.configObject = ConfigurationProvider.getProvider(YamlConfiguration.class);

            this.loadedConfig = configObject.load(configFile);

            File scriptsFolder = new File(instanceIn.getDataFolder().getAbsolutePath(), "scripts");
            this.scriptsFolderPath = scriptsFolder.toPath();

            if (!scriptsFolder.exists()) {

                boolean directoryCreation = scriptsFolder.mkdir();

                if (!directoryCreation) {
                    instanceIn.error("Error creating scripts folder! Global scripts will not work.");
                }

            }

            if (scriptsFolder.exists()) {

                for (File file : scriptsFolder.listFiles(File::isFile)) {

                    String name = file.getName();

                    if (name.endsWith(".sk")) {
                        availableScripts.add(name);
                    }
                }
            }

        } catch (IOException e) {
            instanceIn.error("There was an error getting the config!");

            e.printStackTrace();
        }

    }

    public ArrayList<String> getAvailableScripts() {
        return availableScripts;
    }

    public Path getScriptsFolderPath() {
        return scriptsFolderPath;
    }

    public void save(String node, Object value) throws IOException {
        loadedConfig.set(node, value);
        configObject.save(loadedConfig, configFile);
    }

    public String getString(String node) {
        return loadedConfig.getString(node);
    }

    public boolean getBoolean(String node) {
        return loadedConfig.getBoolean(node);
    }

    public int getInt(String node) {
        return loadedConfig.getInt(node);
    }

    public List<Integer> getIntList(String node) {
        return loadedConfig.getIntList(node);
    }
}
