package me.sharyx.blackstorage.Config;

import me.sharyx.blackstorage.BlackStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConfigManager {

    private final BlackStorage plugin;
    private File configFile;
    private FileConfiguration config;


    public ConfigManager(BlackStorage plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        loadConfig();
        mergeDefaults();
    }

    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void mergeDefaults() {
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);
            config.options().copyDefaults(true);
            saveConfig();
        }
    }

    public void reloadConfig() {
        loadConfig();
        mergeDefaults();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie udało się zapisać pliku konfiguracyjnego!");
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        return config.getString("Prefix") + " ";
    }

    public int getLimit(String item) {
        return config.getInt("Limits." + item + ".limit");
    }

    public HashMap<String, ItemLimitData> getAllLimits() {
        if (!config.isConfigurationSection("Limits")) {
            throw new IllegalStateException("Sekcja 'Limits' nie istnieje w konfiguracji!");
        }

        ConfigurationSection limitsSection = config.getConfigurationSection("Limits");
        HashMap<String, ItemLimitData> limitsMap = new HashMap<>();

        for (String itemKey : limitsSection.getKeys(false)) {
            ConfigurationSection itemSection = limitsSection.getConfigurationSection(itemKey);
            if (itemSection == null) {
                plugin.getLogger().warning("Brak sekcji dla przedmiotu: " + itemKey);
                continue;
            }

            try {
                int limit = itemSection.getInt("limit");
                int slot = itemSection.getInt("slot");
                int page = itemSection.getInt("page");
                String name = itemSection.getString("name");

                limitsMap.put(itemKey.toLowerCase(), new ItemLimitData(limit, slot, page, name, itemSection.getName()));
            } catch (Exception e) {
                plugin.getLogger().warning("Nieprawidłowe dane dla przedmiotu: " + itemKey);
            }
        }

        return limitsMap;
    }


    // Getter pełnego configa, gdyby gdzieś indziej był potrzebny
    public FileConfiguration getConfig() {
        return config;
    }
}
