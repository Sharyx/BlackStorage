package me.sharyx.blackstorage.Events;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener implements Listener {

    @EventHandler
    public void JoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerStorage playerStorage = new PlayerStorage(BlackStorage.getInstance());
        playerStorage.createPlayerData(player);
        playerStorage.loadPlayerToMemory(player);
    }
}
