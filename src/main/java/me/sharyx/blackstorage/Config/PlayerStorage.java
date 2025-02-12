package me.sharyx.blackstorage.Config;

import com.google.gson.*;
import me.sharyx.blackstorage.BlackStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PlayerStorage {

    private static JsonObject jsonObject;
    private static String source = "plugins/BlackStorage/players.json";

    public static HashMap<Player, HashMap<String, Integer>> playersStorage = new HashMap<>();

    public void CreateFile(String name) {
        File file = new File(BlackStorage.getInstance().getDataFolder(), name);
        try {
            if(!file.exists()) {
                file.createNewFile();
                FileWriter writer = new FileWriter(source);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write("{}");
                bufferedWriter.close();

                String content = new String(Files.readAllBytes(Paths.get(source)));
                JsonElement jsonElement = JsonParser.parseString(content);
                this.jsonObject = jsonElement.getAsJsonObject();
            }
            try {
                String content = new String(Files.readAllBytes(Paths.get(source)));
                JsonElement jsonElement = JsonParser.parseString(content);
                this.jsonObject = jsonElement.getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, Integer> getItemsFromStorageJSON(Player player) {
        String uuid = player.getUniqueId().toString();
        JsonElement element = jsonObject.get(uuid);

        if (element == null || !element.isJsonObject()) {
            return null;
        }

        JsonObject jsonObjectForPlayer = element.getAsJsonObject();

        HashMap<String, Integer> playerLimits = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObjectForPlayer.entrySet()) {
            playerLimits.put(entry.getKey(), entry.getValue().getAsInt());
        }

        return playerLimits;
    }

    public static void updateStorageJSON(Player player) {
        jsonObject.add(player.getUniqueId().toString(), new Gson().toJsonTree(playersStorage.get(player)));
        try (FileWriter writer = new FileWriter(source)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jsonObject);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeItemFromInv(Player player, String item) {
        int amount = PlayerStorage.PlayerCheckItems(player, item);
        if (amount > 0) {
            player.getOpenInventory().getBottomInventory().remove(Material.valueOf(item.toUpperCase()));
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            if(item.toLowerCase().equals("totem_of_undying")) {
                for(int i = 0; i < Config.getLimit("totem_of_undying"); i++) {
                    player.getOpenInventory().getBottomInventory().addItem(new ItemStack(Material.valueOf(item.toUpperCase()), 1));
                }
            } else {
                player.getOpenInventory().getBottomInventory().addItem(new ItemStack(Material.valueOf(item.toUpperCase()), Config.getLimit(item)));
            }
            PlayerStorage.addItemCount(player, item, amount);
            player.updateInventory();
        }
    }

    public static void replishAllItems(Player player) {
        Inventory inventory = player.getInventory();
        int amount[] = {0, 0, 0, 0};
        int toReplish[] = {0, 0, 0, 0};
        for(ItemStack itemStack : inventory.getContents()) {
            if(itemStack == null) continue;
            if (itemStack.getType() == Material.GOLDEN_APPLE) amount[0] = amount[0] + itemStack.getAmount();
            else if (itemStack.getType() == Material.ENCHANTED_GOLDEN_APPLE) amount[1] = amount[1] + itemStack.getAmount();
            else if (itemStack.getType() == Material.ENDER_PEARL) amount[1] = amount[1] + itemStack.getAmount();
            else if (itemStack.getType() == Material.TOTEM_OF_UNDYING) amount[1] = amount[1] + itemStack.getAmount();
        }
        toReplish[0] = Config.getLimit("golden_apple") - amount[0];
        toReplish[1] = Config.getLimit("enchanted_golden_apple") - amount[1];
        toReplish[2] = Config.getLimit("ender_pearl") - amount[2];
        toReplish[3] = Config.getLimit("totem_of_undying") - amount[3];
        for(int i = 0; i < toReplish[0]; i++) {
            getItemFromStorage(player, "golden_apple", false);
        }
        for(int i = 0; i < toReplish[1]; i++) {
            getItemFromStorage(player, "enchanted_golden_apple", false);
        }
        for(int i = 0; i < toReplish[2]; i++) {
            getItemFromStorage(player, "ender_pearl", false);
        }
        for(int i = 0; i < toReplish[3]; i++) {
            getItemFromStorage(player, "totem_of_undying", false);
        }

    }
    public static void removeItemCount(Player player, String item, int howMany) {
        int count = playersStorage.get(player).get(item);
        count = count - howMany;
        playersStorage.get(player).put(item, count);
        updateStorageJSON(player);
    }

    public static void addItemCount(Player player, String item, int howMany) {
        int count = playersStorage.get(player).get(item);
        count = count + howMany;
        playersStorage.get(player).put(item, count);
        updateStorageJSON(player);
    }

    public static int PlayerCheckItems(Player player, String item) {
        Inventory inventory = player.getOpenInventory().getBottomInventory();
        int amount = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().equals(Material.valueOf(item.toUpperCase()))) {
                amount = amount + itemStack.getAmount();
            }
        }
        if(amount > Config.getLimit(item.toLowerCase())) {
            player.sendMessage(Config.getPrefix() + ChatColor.RED + " Osiągnąłeś limit tego przedmiotu w ekwipunku: " + ChatColor.WHITE + Config.getLimit(item.toLowerCase()));
            player.updateInventory();
            return amount - Config.getLimit(item.toLowerCase());
        }
        return 0;
    }

    public static boolean isEnoughItem(Player player, String item) {
        int count = playersStorage.get(player).get(item);
        int amount = 0;
        if (count <= 0) return false;
        for(ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack == null) continue;
            if(itemStack.getType().toString().equals(item.toUpperCase())) {
                amount = amount + itemStack.getAmount();
            }
        }
        if (amount >= Config.getLimit( item )) {
            player.sendMessage(Config.getPrefix() + ChatColor.RED + " Osiągnąłeś limit tego przedmiotu w ekwipunku: " + ChatColor.WHITE + Config.getLimit(item));
            return false;
        }
        return true;
    }

    public static void getItemFromStorage(Player player, String item, boolean sendMessage) {
        if(isEnoughItem(player, item)) {
            boolean hasSameItem = false;
            Inventory inventory = player.getInventory();
            ItemStack itemStack = new ItemStack(Material.valueOf(item.toUpperCase()));
            for (ItemStack itemInInv : inventory.getContents()) {
                if (itemInInv == null) continue;
                if(itemInInv.getType().equals(itemStack.getType())) {
                    hasSameItem = true;
                    break;
                }
            }
            if(inventory.firstEmpty() == -1 && !hasSameItem) {
                if(sendMessage == true) player.sendMessage(Config.getPrefix() + ChatColor.RED + " Nie masz wystarczająco miejsca w ekwipunku");
                return;
            }

            removeItemCount(player, item, 1);
            inventory.addItem(itemStack);
        }
    }


    public static void createPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();

        if (jsonObject.get(uuid) == null) {
            Map<String, Integer> items = new HashMap<>();
            items.put("golden_apple", 0);
            items.put("enchanted_golden_apple", 0);
            items.put("totem_of_undying", 0);
            items.put("ender_pearl", 0);
            jsonObject.add(uuid, new Gson().toJsonTree(items));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jsonObject);
            try (FileWriter fileWriter = new FileWriter(source)) {
                fileWriter.write(json);
                Bukkit.getLogger().info("Poprawnie utworzono nowego użytkownika w pliku JSON.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
