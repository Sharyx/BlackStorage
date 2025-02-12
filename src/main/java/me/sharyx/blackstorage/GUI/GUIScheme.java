package me.sharyx.blackstorage.GUI;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.Config.Config;
import me.sharyx.blackstorage.Config.PlayerStorage;
import me.sharyx.blackstorage.FoxInventory.FoxInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class GUIScheme {

    public Inventory createInventory( Player player ) {
        Inventory inventory = FoxInventory.getBuilder(BlackStorage.getInstance(), 3, ChatColor.RED + "Schowek").setPrevention()
        .setItem(Material.GOLDEN_APPLE, 10, ChatColor.YELLOW + "Złote jabłko", List.of(ChatColor.GOLD + "Posiadasz: " + ChatColor.WHITE + PlayerStorage.playersStorage.get( player ).get("golden_apple"), "", ChatColor.RED + "Limit 16 złotych jabłek"), player1 -> PlayerStorage.getItemFromStorage(player, "golden_apple", true))
        .setItem(Material.ENCHANTED_GOLDEN_APPLE, 12, ChatColor.YELLOW + "Zaklęte złote jabłko", List.of(ChatColor.GOLD + "Posiadasz: " + ChatColor.WHITE + PlayerStorage.playersStorage.get( player ).get("enchanted_golden_apple"), "", ChatColor.RED + "Limit 2 zaklęte złote jabłka"), player1 -> PlayerStorage.getItemFromStorage(player, "enchanted_golden_apple", true))
        .setItem(Material.TOTEM_OF_UNDYING, 14, ChatColor.YELLOW + "Totem nieśmiertelności", List.of(ChatColor.GOLD + "Posiadasz: " + ChatColor.WHITE + PlayerStorage.playersStorage.get( player ).get("totem_of_undying"), "", ChatColor.RED + "Limit 2 totemy nieśmiertelności"), player1 -> PlayerStorage.getItemFromStorage(player, "totem_of_undying", true))
        .setItem(Material.ENDER_PEARL, 16, ChatColor.YELLOW + "Ender perła", List.of(ChatColor.GOLD + "Posiadasz: " + ChatColor.WHITE + PlayerStorage.playersStorage.get( player ).get("ender_pearl"), "", ChatColor.RED + "Limit 10 ender pereł"), player1 -> PlayerStorage.getItemFromStorage(player, "ender_pearl", true))
        .setItem(Material.CHEST, 26, ChatColor.YELLOW + "Uzupełnij zapasy", List.of(""), player1 -> PlayerStorage.replishAllItems(player))
        .fill(Material.GRAY_STAINED_GLASS_PANE, 0, 1, 2, 3, 4, 5, 6, 7, 8)
        .fill(Material.GRAY_STAINED_GLASS_PANE,  18, 19, 20, 21, 22, 23, 24, 25)
        .fill(Material.YELLOW_STAINED_GLASS_PANE, 9, 11, 13, 15, 17)
        .buildBukkitInventory();

        return inventory;
    }

}
