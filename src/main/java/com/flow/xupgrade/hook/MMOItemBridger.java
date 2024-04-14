package com.flow.xupgrade.hook;

import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MMOItemBridger {

    private Type type;
    private String id;

    public MMOItemBridger(String type, String id) {
        this.type = Type.get(type);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public ItemStack build() {
        if (MMOItemHook.findMMOItem(this.type, this.id).isPresent())
            return MMOItemHook.findMMOItem(this.type, this.id).get().newBuilder().build();
        return new ItemStack(Material.AIR);
    }
}
