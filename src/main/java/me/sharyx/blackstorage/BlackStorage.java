package me.sharyx.blackstorage;

import me.sharyx.blackstorage.CMD.StorageCMD;
import me.sharyx.blackstorage.Config.ConfigManager;
import me.sharyx.blackstorage.Events.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlackStorage extends JavaPlugin {

    private static BlackStorage instance;

    private ConfigManager configManager;

    public static BlackStorage getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        PlayerStorage playerStorageObject = new PlayerStorage(getInstance());
        playerStorageObject.initializeStorageFile();

        this.configManager = new ConfigManager(this);

        //Events
        getServer().getPluginManager().registerEvents(new OnJoinListener(), this);
        getServer().getPluginManager().registerEvents(new OnQuitListener(), this);
        getServer().getPluginManager().registerEvents(new OnInventoryClick(getInstance()), this);
        getServer().getPluginManager().registerEvents(new OnInventoryClose(getInstance()), this);
        getServer().getPluginManager().registerEvents(new OnPickupListener(getInstance()), this);

        //Commands
        this.getCommand("schowek").setExecutor(new StorageCMD());

    }

    @Override
    public void onDisable() {

    }
}
