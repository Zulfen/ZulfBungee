package tk.zulfengaming.zulfbungee.bungeecord.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class YamlConfig implements ProxyConfig {

    private Path scriptsFolderPath;
    private File scriptsFolder;

    private Configuration loadedConfig;

    @SuppressWarnings("UnstableApiUsage")
    public YamlConfig(ZulfBungeecord instanceIn) {

        File configFile = new File(instanceIn.getDataFolder(), "config.yml");

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
                             OutputStream os = Files.newOutputStream(configFile.toPath())) {
                            ByteStreams.copy(is, os);
                        }
                    }

                } catch (IOException e) {
                    instanceIn.error("There was an error copying the default config:");
                    e.printStackTrace();
                }

            ConfigurationProvider configObject = ConfigurationProvider.getProvider(YamlConfiguration.class);

            this.loadedConfig = configObject.load(configFile);

            this.scriptsFolder = new File(instanceIn.getDataFolder().getAbsolutePath(), "scripts");
            this.scriptsFolderPath = scriptsFolder.toPath();

            if (!scriptsFolder.exists()) {

                boolean directoryCreation = scriptsFolder.mkdir();

                if (!directoryCreation) {
                    instanceIn.error("Error creating scripts folder! Global scripts will not work.");
                }

            }


        } catch (IOException e) {
            instanceIn.error("There was an error getting the config!");

            e.printStackTrace();
        }

    }

    public ArrayList<String> getScripts() {

        ArrayList<String> cachedScripts = new ArrayList<>();

        if (scriptsFolder.exists()) {

            File[] files = scriptsFolder.listFiles(File::isFile);

            if (files != null) {

                for (File file : files) {

                    String name = file.getName();

                    if (name.endsWith(".sk")) {
                        cachedScripts.add(name);
                    }

                }
            }
        }

        return cachedScripts;
    }

    public Path getScriptsFolderPath() {
        return scriptsFolderPath;
    }

    public Path getScriptPath(String scriptNameIn) {
        return scriptsFolderPath.resolve(scriptNameIn);
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
