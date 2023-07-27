package com.zulfen.zulfbungee.universal.config;

import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ProxyConfig<P, T> {

    protected final Path scriptsFolder;
    protected final Path configFile;

    private final ArrayList<String> activeScripts = new ArrayList<>();

    protected ProxyConfig(ZulfBungeeProxy<P, T> instanceIn) {

        Path dataFolder = instanceIn.getPluginFolder();
        this.configFile = dataFolder.resolve("config.yml");

        if (!Files.exists(dataFolder)) {
            try {
                Files.createDirectory(dataFolder);
            } catch (IOException e) {
                throw new RuntimeException("Could not create plugin's folder!");
            }
        }


        if (!Files.exists(configFile)) {

            try {

                InputStream jarConfig = getClass().getClassLoader().getResourceAsStream("proxy.yml");

                if (jarConfig != null) {
                    Files.copy(jarConfig, // This will copy your default config.yml from the jar
                            configFile);
                }


            } catch (IOException e) {
                instanceIn.error("There was an error copying the default config:");
                e.printStackTrace();

            }

        }

        this.scriptsFolder = dataFolder.resolve("scripts");

        if (!Files.exists(scriptsFolder)) {

            try {
                Files.createDirectory(scriptsFolder);
            } catch (IOException e) {
                throw new RuntimeException("Error creating scripts folder! Global scripts will not work.", e);
            }

        }


    }

    public List<Path> getScriptPaths() {

        try (Stream<Path> pathStream = Files.list(scriptsFolder)) {

            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".sk"))
                    .collect(Collectors.toList());


        } catch (IOException e) {
            throw new RuntimeException("Could not list files in scripts directory!", e);
        }


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
        return scriptsFolder;
    }

    public abstract String getString(String node);

    public abstract boolean getBoolean(String node);

    public abstract int getInt(String node);

    public abstract List<Integer> getIntList(String node);

}
