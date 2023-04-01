package tk.zulfengaming.zulfbungee.universal.config;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ProxyConfig<P> {

    protected final File scriptsFolder;
    protected final Path scriptsFolderPath;
    protected final File configFile;

    private final ArrayList<String> activeScripts = new ArrayList<>();

    protected ProxyConfig(ZulfBungeeProxy<P> instanceIn) {

        File dataFolder = instanceIn.getPluginFolder();
        this.configFile = new File(dataFolder, "config.yml");

        if (!dataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolder.mkdir();

        }


        if (!configFile.exists()) {

            try {

                InputStream jarConfig = getClass().getClassLoader().getResourceAsStream("proxy.yml");

                if (jarConfig != null) {
                    Files.copy(jarConfig, // This will copy your default config.yml from the jar
                            configFile.toPath());
                }


            } catch (IOException e) {
                instanceIn.error("There was an error copying the default config:");
                e.printStackTrace();

            }

        }

        this.scriptsFolder = new File(dataFolder, "scripts");
        this.scriptsFolderPath = scriptsFolder.toPath();

        if (!scriptsFolder.exists()) {

            boolean directoryCreation = scriptsFolder.mkdir();

            if (!directoryCreation) {
                instanceIn.error("Error creating scripts folder! Global scripts will not work.");
            }

        }


    }

    public Map<String, Path> getScriptPaths() {

        HashMap<String, Path> cachedScripts = new HashMap<>();

        if (scriptsFolder.exists()) {

            File[] files = scriptsFolder.listFiles(File::isFile);

            if (files != null) {

                for (File file : files) {

                    String name = file.getName();

                    if (name.endsWith(".sk")) {
                        cachedScripts.put(name, file.toPath());
                    }

                }
            }
        }

        return cachedScripts;

    }

    public void registerScript(String nameIn) {
        activeScripts.add(nameIn);
    }

    public void unregisterScript(String nameIn) {
        activeScripts.remove(nameIn);
    }

    public boolean isScriptActive(String nameIn) {
        return activeScripts.contains(nameIn);
    }

    public Path getScriptsFolderPath() {
        return scriptsFolderPath;
    }

    public abstract String getString(String node);

    public abstract boolean getBoolean(String node);

    public abstract int getInt(String node);

    public abstract List<Integer> getIntList(String node);

}
