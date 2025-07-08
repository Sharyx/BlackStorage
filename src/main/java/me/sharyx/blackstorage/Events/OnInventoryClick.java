package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class OnInventoryClick implements Listener {

    private final BlackStorage plugin;
    private final PlayerStorage playerStorage;

    public OnInventoryClick(BlackStorage plugin) {
        this.plugin = plugin;
        this.playerStorage = new PlayerStorage(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        Set<String> trackedItems = plugin.getConfigManager().getConfig()
                .getConfigurationSection("Limits").getKeys(false);

        trackedItems.forEach(item ->
                plugin.getServer().getScheduler().runTaskLater(plugin,
                        () -> playerStorage.removeItemsFromInventory(player, item), 1L)
        );
    }
}