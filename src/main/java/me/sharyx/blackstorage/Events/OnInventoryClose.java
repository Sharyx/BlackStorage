package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.Config.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class OnInventoryClose implements Listener {

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(BlackStorage.getInstance(), () -> {
            PlayerStorage.removeItemFromInv(player, "golden_apple");
            PlayerStorage.removeItemFromInv(player, "enchanted_golden_apple");
            PlayerStorage.removeItemFromInv(player, "ender_pearl");
            PlayerStorage.removeItemFromInv(player, "totem_of_undying");
        }, 1L); // 1 tick delay
    }
}
