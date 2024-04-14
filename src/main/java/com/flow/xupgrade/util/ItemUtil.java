package com.flow.xupgrade.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtil {

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

    public static ItemStack updateModelData(ItemStack item, int customModelData) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        item.setItemMeta(itemMeta);
        return item;
    }

}
