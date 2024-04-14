package com.flow.xupgrade.command;

import com.flow.xupgrade.hook.MMOItemBridger;
import com.flow.xupgrade.hook.MMOItemHook;
import com.flow.xupgrade.inventory.BukkitUpgradeInventory;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class Basic implements CommandExecutor {

    public static ItemStack setItem(Material type, String name, int cdata) {
        ItemStack item = new ItemStack(type, 1);
        ItemMeta data = item.getItemMeta();
        data.setDisplayName(name);
        if (cdata != 0)
            data.setCustomModelData(cdata);
        item.setItemMeta(data);
        return item;
    }

    public static ItemStack updateName(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack updateLore(ItemStack item, List<String> name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Upgrade] 이 명령어는 콘솔에서 사용할 수 없습니다.");
            return true;
        }

        BukkitUpgradeInventory upgradeInventory = new BukkitUpgradeInventory(player);
        upgradeInventory.openInventory(player);
        return true;
    }



}
