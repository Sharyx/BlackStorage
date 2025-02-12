package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.Config.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener implements Listener {

    @EventHandler
    public void JoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerStorage.createPlayerData(player);
        PlayerStorage.playersStorage.put(player, PlayerStorage.getItemsFromStorageJSON(player));
    }
}
