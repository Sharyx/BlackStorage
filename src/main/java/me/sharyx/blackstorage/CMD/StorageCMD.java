package me.sharyx.blackstorage.CMD;

import me.sharyx.blackstorage.GUI.GUIScheme;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StorageCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            GUIScheme guiScheme = new GUIScheme();
            Inventory inventory = guiScheme.createInventory(player);
            player.openInventory(inventory);
        } else {
            sender.sendMessage(ChatColor.RED + "Z komendy może skorzystać tylko gracz");
        }
        return true;
    }

}
