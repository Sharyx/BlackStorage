package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.Config.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnInventoryClick implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTaskLater(BlackStorage.getInstance(), () -> {
            PlayerStorage.removeItemFromInv(player, "golden_apple");
            PlayerStorage.removeItemFromInv(player, "enchanted_golden_apple");
            PlayerStorage.removeItemFromInv(player, "ender_pearl");
            PlayerStorage.removeItemFromInv(player, "totem_of_undying");
        }, 1L); // 1 tick delay

/*        if (itemStack == null) return;
        if (itemStack.getType() == Material.AIR) itemStack = event.getCursor();
        if (itemStack == null) return;
        if (!(itemStack.getType() == Material.GOLDEN_APPLE) && !(itemStack.getType() == Material.ENCHANTED_GOLDEN_APPLE) && !(itemStack.getType() == Material.ENDER_PEARL) && !(itemStack.getType() == Material.TOTEM_OF_UNDYING)) return;
        if (event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            Bukkit.getScheduler().runTaskLater(BlackStorage.getInstance(), () -> {
                int amount = PlayerStorage.PlayerCheckItems(player, "golden_apple", true);
                if (amount > 0) {
                    player.getOpenInventory().getBottomInventory().remove(Material.GOLDEN_APPLE);
                    player.getOpenInventory().getBottomInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, Config.getLimit("golden_apple")));
                    PlayerStorage.addItemCount(player, "golden_apple", amount);
                    player.updateInventory();
                }
            }, 1L); // 1 tick delay
            return;
        }
        int amount = PlayerStorage.PlayerCheckItems(player, itemStack.getType().toString(), false);
        if (amount > 0) {
            event.setCancelled(true);
            player.getInventory().remove(Material.valueOf(itemStack.getType().toString().toUpperCase()));
            player.getInventory().addItem(new ItemStack(Material.valueOf(itemStack.getType().toString().toUpperCase()), Config.getLimit(itemStack.getType().toString().toLowerCase())));
            PlayerStorage.addItemCount(player, itemStack.getType().toString().toLowerCase(), amount);
            player.updateInventory();
        } */
    }
}
