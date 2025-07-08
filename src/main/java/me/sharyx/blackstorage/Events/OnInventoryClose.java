package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Set;

public class OnInventoryClose implements Listener {

    private final BlackStorage plugin;
    private final PlayerStorage playerStorage;

    public OnInventoryClose(BlackStorage plugin) {
        this.plugin = plugin;
        this.playerStorage = new PlayerStorage(plugin);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        Set<String> trackedItems = plugin.getConfigManager().getConfig().getConfigurationSection("Limits").getKeys(false);

        trackedItems.forEach(item ->
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                        playerStorage.removeItemsFromInventory(player, item), 1L)
        );
    }
}