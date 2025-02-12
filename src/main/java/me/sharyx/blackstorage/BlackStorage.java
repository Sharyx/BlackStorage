package me.sharyx.blackstorage;

import me.sharyx.blackstorage.CMD.StorageCMD;
import me.sharyx.blackstorage.Config.Config;
import me.sharyx.blackstorage.Config.PlayerStorage;
import me.sharyx.blackstorage.Events.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlackStorage extends JavaPlugin {

    private static BlackStorage instance;

    public static BlackStorage getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PlayerStorage playerStorageObject = new PlayerStorage();
        this.saveDefaultConfig();
        Config.setConfigurationFile(this.getConfig());
        Config.getConfigurationFile().options().copyDefaults(true);
        saveConfig();
        getDataFolder().mkdir();
        playerStorageObject.CreateFile("players.json");

        //Events
        getServer().getPluginManager().registerEvents(new OnJoinListener(), this);
        getServer().getPluginManager().registerEvents(new OnQuitListener(), this);
        getServer().getPluginManager().registerEvents(new OnInventoryClick(), this);
        getServer().getPluginManager().registerEvents(new OnInventoryClose(), this);
        getServer().getPluginManager().registerEvents(new OnPickupListener(), this);

        //Commands
        this.getCommand("schowek").setExecutor(new StorageCMD());

    }

    @Override
    public void onDisable() {

    }
}
