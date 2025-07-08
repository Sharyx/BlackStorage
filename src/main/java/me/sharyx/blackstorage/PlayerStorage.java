package me.sharyx.blackstorage;

import com.google.gson.*;
import me.sharyx.blackstorage.Config.ConfigManager;
import me.sharyx.blackstorage.Config.ItemLimitData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerStorage {
    private static final String STORAGE_FILE = "plugins/BlackStorage/players.json";
    private static JsonObject jsonObject;
    private static final Map<UUID, Map<String, Integer>> playersStorage = new HashMap<>();

    private final BlackStorage plugin;
    private final ConfigManager configManager;

    public PlayerStorage(BlackStorage plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public void initializeStorageFile() {
        File file = new File(STORAGE_FILE);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{}");
                }
            }
            loadJsonData();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize storage file", e);
        }
    }

    private void loadJsonData() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(STORAGE_FILE)), StandardCharsets.UTF_8);
        if (content.trim().isEmpty()) {
            jsonObject = new JsonObject();
            return;
        }
        JsonElement jsonElement = JsonParser.parseString(content);
        jsonObject = jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : new JsonObject();
    }

    public Map<String, Integer> getPlayerItems(Player player) {
        String uuid = player.getUniqueId().toString();
        JsonElement element = jsonObject.get(uuid);

        if (element == null || !element.isJsonObject()) {
            return null;
        }

        Map<String, Integer> playerLimits = new HashMap<>();
        JsonObject playerObject = element.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : playerObject.entrySet()) {
            playerLimits.put(entry.getKey(), entry.getValue().getAsInt());
        }

        return playerLimits;
    }

    public void updatePlayerStorage(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> playerData = playersStorage.get(uuid);
        if (playerData == null) return;

        jsonObject.add(uuid.toString(), new Gson().toJsonTree(playerData));
        saveToFile();
    }

    private void saveToFile() {
        synchronized (STORAGE_FILE.intern()) {
            try (FileWriter writer = new FileWriter(STORAGE_FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonObject, writer);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save player storage", e);
            }
        }
    }

    public void removeItemsFromInventory(Player player, String item) {
        int excessAmount = getExcessItemsAmount(player, item);
        if (excessAmount <= 0) return;

        Material material = getMaterialSafe(item);
        if (material == null) return;

        Inventory inventory = player.getOpenInventory().getBottomInventory();
        int totalRemoved = 0;

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() == material) {
                int remove = Math.min(itemStack.getAmount(), excessAmount - totalRemoved);
                itemStack.setAmount(itemStack.getAmount() - remove);
                totalRemoved += remove;
                if (totalRemoved >= excessAmount) break;
            }
        }

        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand.getType() == material && totalRemoved < excessAmount) {
            int remove = Math.min(offhand.getAmount(), excessAmount - totalRemoved);
            offhand.setAmount(offhand.getAmount() - remove);
            totalRemoved += remove;
        }

        addItemCount(player, item, totalRemoved);
        player.updateInventory();
    }

    public void replenishAllItems(Player player) {
        Inventory inventory = player.getInventory();
        Map<String, ItemLimitData> itemsLimits = BlackStorage.getInstance().getConfigManager().getAllLimits();
        Map<String, Integer> currentAmounts = new HashMap<>();

        itemsLimits.keySet().forEach(item -> currentAmounts.put(item, 0));

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;

            String itemName = normalizeItemName(itemStack.getType().name());

            if (itemsLimits.containsKey(itemName)) {
                currentAmounts.put(itemName, currentAmounts.get(itemName) + itemStack.getAmount());
            }
        }

        itemsLimits.forEach((itemName, limit) -> {
            int current = currentAmounts.getOrDefault(itemName, 0);
            int toReplenish = limit.getLimit() - current;

            for (int i = 0; i < toReplenish; i++) {
                getItemFromStorage(player, itemName, false);
            }
        });
    }

    public void removePlayerFromMemory(Player player) {
        playersStorage.remove(player.getUniqueId());
    }

    public void loadPlayerToMemory(Player player) {
        playersStorage.put(player.getUniqueId(), getPlayerItems(player));
    }

    public void removeItemCount(Player player, String item, int amount) {
        Map<String, Integer> playerData = playersStorage.get(player.getUniqueId());
        if (playerData == null || !playerData.containsKey(item)) return;

        int newCount = playerData.get(item) - amount;
        playerData.put(item, newCount);
        updatePlayerStorage(player);
    }

    public void addItemCount(Player player, String item, int amount) {
        Map<String, Integer> playerData = playersStorage.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        int newCount = playerData.getOrDefault(item, 0) + amount;
        playerData.put(item, newCount);
        updatePlayerStorage(player);
    }

    public int getExcessItemsAmount(Player player, String item) {
        Inventory inventory = player.getOpenInventory().getBottomInventory();
        int amount = 0;

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().equals(getMaterialSafe(item))) {
                amount += itemStack.getAmount();
            }
        }

        int limit = configManager.getLimit(normalizeItemName(item));
        if (amount > limit) {
            player.sendMessage(configManager.getPrefix() + ChatColor.RED + "Osiągnąłeś limit tego przedmiotu w ekwipunku: " + ChatColor.WHITE + limit);
            player.updateInventory();
            return amount - limit;
        }
        return 0;
    }

    public boolean canTakeItem(Player player, String item) {
        Map<String, Integer> playerData = playersStorage.get(player.getUniqueId());
        if (playerData == null || playerData.getOrDefault(item, 0) <= 0) {
            return false;
        }

        int inventoryAmount = countItemsInInventory(player, item);
        int limit = configManager.getLimit(item);

        if (inventoryAmount >= limit) {
            player.sendMessage(configManager.getPrefix() + ChatColor.RED +
                    "Osiągnąłeś limit tego przedmiotu w ekwipunku: " + ChatColor.WHITE + limit);
            return false;
        }
        return true;
    }

    private int countItemsInInventory(Player player, String item) {
        int inventoryAmount = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().toString().equalsIgnoreCase(item)) {
                inventoryAmount += itemStack.getAmount();
            }
        }
        return inventoryAmount;
    }

    public void getItemFromStorage(Player player, String item, boolean sendMessage) {
        if (!canTakeItem(player, item)) return;

        Material material = getMaterialSafe(item);
        if (material == null) {
            plugin.getLogger().warning("Invalid item type: " + item);
            return;
        }

        Inventory inventory = player.getInventory();
        boolean hasSpace = inventory.firstEmpty() != -1 || hasItem(inventory, material);

        if (!hasSpace) {
            if (sendMessage) {
                player.sendMessage(configManager.getPrefix() + ChatColor.RED + "Nie masz wystarczająco miejsca w ekwipunku");
            }
            return;
        }

        removeItemCount(player, item, 1);
        inventory.addItem(new ItemStack(material));
    }

    private boolean hasItem(Inventory inventory, Material material) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                return true;
            }
        }
        return false;
    }

    public void createPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();

        if (!jsonObject.has(uuid)) {
            Map<String, Integer> defaultItems = new HashMap<>();
            configManager.getAllLimits().keySet().forEach(item -> defaultItems.put(item, 0));

            jsonObject.add(uuid, new Gson().toJsonTree(defaultItems));
            playersStorage.put(player.getUniqueId(), defaultItems);
            saveToFile();
            plugin.getLogger().info("Created new player data for: " + player.getName());
        }
    }

    private String normalizeItemName(String item) {
        return item.toLowerCase();
    }

    private Material getMaterialSafe(String item) {
        try {
            return Material.valueOf(item.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}