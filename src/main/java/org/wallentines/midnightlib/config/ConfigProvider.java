package org.wallentines.midnightlib.config;

import java.io.File;
import java.io.InputStream;

@Deprecated
public interface ConfigProvider {

    ConfigSection loadFromFile(File file);

    ConfigSection loadFromStream(InputStream stream);

    ConfigSection loadFromString(String string);

    void saveToFile(ConfigSection config, File file);

    String saveToString(ConfigSection config);

    String getFileExtension();
}

