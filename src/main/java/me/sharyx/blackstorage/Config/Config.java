package me.sharyx.blackstorage.Config;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private static FileConfiguration config;
    public static FileConfiguration getConfigurationFile() {
        return config;
    }
    public static void setConfigurationFile(FileConfiguration config) {
        Config.config = config;
    }

    public static String getPrefix() {
        FileConfiguration config = getConfigurationFile();
        return config.getString("Prefix");
    }

    public static int getLimit(String item) {
        FileConfiguration config = getConfigurationFile();
        return config.getInt("Limits." + item);
    }
}
