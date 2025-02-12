package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.Config.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class OnPickupListener implements Listener {

    @EventHandler
    public void PickupEvent(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Bukkit.getScheduler().runTaskLater(BlackStorage.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerStorage.removeItemFromInv(player, "golden_apple");
                PlayerStorage.removeItemFromInv(player, "enchanted_golden_apple");
                PlayerStorage.removeItemFromInv(player, "ender_pearl");
                PlayerStorage.removeItemFromInv(player, "totem_of_undying");
            }
        }, 1L);
    }
}
