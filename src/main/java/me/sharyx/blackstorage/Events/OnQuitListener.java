package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnQuitListener implements Listener {

    @EventHandler
    public void QuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerStorage playerStorage = new PlayerStorage(BlackStorage.getInstance());
        playerStorage.removePlayerFromMemory(player);
    }
}
