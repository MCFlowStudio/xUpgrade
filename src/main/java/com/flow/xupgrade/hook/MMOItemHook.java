package com.flow.xupgrade.hook;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.ItemManager;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MMOItemHook {

    public static Optional<MMOItem> findMMOItem(ItemStack itemStack) {
        ItemManager itemManager = MMOItems.plugin.getItems();
        NBTItem nbtItem = NBTItem.get(itemStack);

        if (nbtItem.hasType() && nbtItem.hasTag("MMOITEMS_ITEM_ID")) {
            // Type 객체를 얻습니다.
            Type type = Type.get(nbtItem.getType());
            String itemID = nbtItem.getString("MMOITEMS_ITEM_ID");

            // 타입과 이름(ID)으로 MMOItem 객체를 얻습니다.
            return Optional.ofNullable(itemManager.getMMOItem(type, itemID));
        }

        return Optional.empty(); // 아이템이 MMOItem에 해당하지 않는 경우
    }

    public static Optional<MMOItemBridger> convertMMOItem(MMOItem item) {
        ItemManager itemManager = MMOItems.plugin.getItems();
        MMOItemBridger mmoItemBridger = new MMOItemBridger(item.getType().getId(), item.getId());
        return Optional.ofNullable(mmoItemBridger);
    }

    public static Optional<MMOItem> findMMOItem(Type type, String id) {
        ItemManager itemManager = MMOItems.plugin.getItems();
        if (itemManager.getMMOItem(type, id) != null)
            return Optional.ofNullable(itemManager.getMMOItem(type, id));

        return Optional.empty(); // 아이템이 MMOItem에 해당하지 않는 경우
    }
}
