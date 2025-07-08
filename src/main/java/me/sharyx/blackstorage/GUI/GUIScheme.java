package me.sharyx.blackstorage.GUI;

import me.sharyx.blackstorage.BlackStorage;
import me.sharyx.blackstorage.Config.ItemLimitData;
import me.sharyx.blackstorage.PlayerStorage;
import me.sharyx.blackstorage.FoxInventory.FoxInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

public class GUIScheme {

    public Inventory createInventory( Player player ) {
        PlayerStorage playerStorage = new PlayerStorage(BlackStorage.getInstance());

        FoxInventory.Builder inventory = FoxInventory.getBuilder(BlackStorage.getInstance(), 3, ChatColor.RED + "Schowek").setPrevention();

        Map<String, ItemLimitData> limits = BlackStorage.getInstance().getConfigManager().getAllLimits();

        for (String item : limits.keySet()) {
            ItemLimitData itemLimitData = limits.get(item);

            Material material;
            try {
                material = Material.valueOf(itemLimitData.getConfigName().toUpperCase());
            } catch (IllegalArgumentException e) {
                BlackStorage.getInstance().getLogger().warning("Invalid material: " + itemLimitData.getConfigName());
                continue;
            }

            int currentAmount = playerStorage.getPlayerItems(player).getOrDefault(itemLimitData.getConfigName(), 0);
            int limit = BlackStorage.getInstance().getConfigManager().getLimit(itemLimitData.getConfigName());

            inventory = inventory.setItem(
                    material,
                    itemLimitData.getSlot(),
                    itemLimitData.getName(),
                    List.of(
                            ChatColor.GOLD + "Posiadasz: " + ChatColor.WHITE + currentAmount,
                            "",
                            ChatColor.RED + "Limit " + limit
                    ),
                    player1 -> playerStorage.getItemFromStorage(player, item, true)
            );
        }
        Inventory buildedInventory = inventory.setItem(Material.CHEST, 26, ChatColor.YELLOW + "Uzupełnij zapasy", List.of(""), player1 -> playerStorage.replenishAllItems(player))
        .fill(Material.YELLOW_STAINED_GLASS_PANE, 0, 1, 2, 3, 4, 5, 6, 7, 8)
        .fill(Material.YELLOW_STAINED_GLASS_PANE,  18, 19, 20, 21, 22, 23, 24, 25)
        .buildBukkitInventory();

        return buildedInventory;
    }

}
