package org.wallentines.midnightlib.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Deprecated
public class FileConfig {

    private static final Logger LOGGER = LogManager.getLogger("FileConfig");

    private final File file;
    private final ConfigProvider provider;

    private ConfigSection root;

    public FileConfig(File f) {

        this(f, ConfigRegistry.INSTANCE.getProviderForFile(f));
    }

    public FileConfig(File f, ConfigProvider prov) {

        this.file = f;
        this.provider = prov;

        root = prov.loadFromFile(f);

    }

    public ConfigSection getRoot() {
        return root;
    }

    public ConfigProvider getProvider() {
        return provider;
    }

    public File getFile() {
        return file;
    }

    public void setRoot(ConfigSection sec) { this.root = sec; }

    public void reload() {
        root = provider.loadFromFile(file);
    }

    public void save() {
        provider.saveToFile(root, file);
    }


    public static FileConfig fromFile(File f) {

        ConfigProvider prov = ConfigRegistry.INSTANCE.getProviderForFile(f);
        if(prov != null) return new FileConfig(f, prov);

        return null;
    }

    public static FileConfig findFile(File[] list, String prefix) {

        if(list == null) return null;

        for(File f : list) {

            int dot = f.getName().lastIndexOf('.');
            if(dot == -1) continue;

            String ext = f.getName().substring(dot);
            String fileName = f.getName().substring(0, dot);

            if(!prefix.equals(fileName)) continue;
            ConfigProvider prov = ConfigRegistry.INSTANCE.getProviderForFileType(ext);

            if(prov != null) {
                return new FileConfig(f, prov);
            }
        }

        return null;
    }

    public static FileConfig findOrCreate(String prefix, File directory, ConfigSection defaults) {

        if(!directory.exists() && !directory.mkdirs()) {
            LOGGER.warn("Unable to create folder " + directory.getAbsolutePath() + "!");
            return null;
        }

        if(!directory.isDirectory()) return null;

        FileConfig out = findFile(directory.listFiles(), prefix);
        if(out != null) {
            out.getRoot().fill(defaults);
            out.save();
            return out;
        }

        ConfigProvider provider = ConfigRegistry.INSTANCE.getDefaultProvider();
        if(provider == null) return null;

        File f = new File(directory, prefix + provider.getFileExtension());
        provider.saveToFile(defaults, f);

        return new FileConfig(f, provider);

    }

    public static FileConfig findOrCreate(String prefix, File directory) {

        return findOrCreate(prefix, directory, new ConfigSection());
    }

}
