package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Set;

public class OnPickupListener implements Listener {

    private final BlackStorage plugin;
    private final PlayerStorage playerStorage;

    public OnPickupListener(BlackStorage plugin) {
        this.plugin = plugin;
        this.playerStorage = new PlayerStorage(plugin);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Set<String> trackedItems = plugin.getConfigManager().getConfig()
                .getConfigurationSection("Limits").getKeys(false);

        String itemName = event.getItem().getItemStack().getType().name().toLowerCase();
        if (trackedItems.contains(itemName)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    trackedItems.forEach(item ->
                            playerStorage.removeItemsFromInventory(player, item)
                    ), 1L);
        }
    }
}