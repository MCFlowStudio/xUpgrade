package com.flow.xupgrade.listener;

import com.flow.xupgrade.Main;
import com.flow.xupgrade.database.ItemData;
import com.flow.xupgrade.hook.MMOItemBridger;
import com.flow.xupgrade.hook.MMOItemHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import static com.flow.xupgrade.Main.*;


public class ClickEvent implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();
        if (!event.getView().getTitle().contains("七七七七七七七七♿"))
            return;
        if (clickedItem == null)
            return;
        if (clickedItem.getType().equals(Material.AIR))
            return;
        if (clickedItem.getItemMeta().getDisplayName().equals("§c취소")) {
            event.setCancelled(true);
            p.closeInventory();
            Main.playSound(p, Sound.BLOCK_LEVER_CLICK);
        }
        if (clickedItem.getItemMeta().getDisplayName().equals("§a강화")) {
            event.setCancelled(true);
            String name = inv.getItem(4).getItemMeta().getDisplayName();
            ItemStack item = inv.getItem(22);
            if (!MMOItemHook.findMMOItem(item).isPresent() || item.getItemMeta().getLore() == null) {
                p.sendMessage(ItemData.getMessage("upgrade-notool"));
                Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                p.closeInventory();
                return;
            }
            String id = MMOItemHook.findMMOItem(item).get().getType().getId() + "@" + MMOItemHook.findMMOItem(item).get().getId();
            if (id == null) {
                p.sendMessage(ItemData.getMessage("upgrade-notool"));
                Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                p.closeInventory();
                return;
            }

            int chance = ItemData.getItemChance(id);

            int cost = 0;
            MMOItemBridger requireItem = null;
            String condition = ItemData.getCondition(id);
            if (condition.equals("==")) {
                String type = ItemData.getType(id);
                if (type.equals("money")) {
                    cost = ItemData.getRequirementMoney(id);
                    if (econ.getBalance(p) < cost) {
                        p.sendMessage(ItemData.getMessage("upgrade-nomoney"));
                        Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                        return;
                    }
                    econ.withdrawPlayer(p, cost);

                } else if (type.equals("item")) {
                    requireItem = ItemData.getItemRequire(id);
                    int requireAmount = ItemData.getRequirementItemAmount(id);
                    ItemStack removeItem = MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build();
                    removeItem.setAmount(requireAmount);
                    if (!p.getInventory().containsAtLeast(MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build(), requireAmount)) {
                        p.sendMessage("§f\u0602§c재료가 부족합니다.");
                        Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                        return;
                    }
                    p.getInventory().removeItem(removeItem);
                }
            } else if (condition.equals("&&")) {
                cost = ItemData.getRequirementMoney(id);
                requireItem = ItemData.getItemRequire(id);
                int requireAmount = ItemData.getRequirementItemAmount(id);
                ItemStack removeItem = MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build();
                removeItem.setAmount(requireAmount);
                if (econ.getBalance(p) < cost) {
                    p.sendMessage(ItemData.getMessage("upgrade-nomoney"));
                    Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                    return;
                }
                if (!p.getInventory().containsAtLeast(MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build(), requireAmount)) {
                    p.sendMessage("§f\u0602§c재료가 부족합니다.");
                    Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                    return;
                }
                econ.withdrawPlayer(p, cost);
                p.getInventory().removeItem(removeItem);
            }

            // Upgrade Logic
            Main.logger.info("강화 시작 (닉네임: " + p.getName() + ", 도구: " + id + ", 금액: " + cost + ")");
            Random random = new Random();
            if (random.nextInt(100) < chance) {
                inv.setItem(22, new ItemStack(Material.AIR, 1));
                p.closeInventory();
                Main.saveLogFiles(p, 2, "강화 성공 (닉네임: " + p.getName() + ", 도구: " + id + ", 금액: " + cost + ", 요구 아이템:" + requireItem +  ")");
                MMOItemBridger output = ItemData.getOutputItem(id);
                String message = ItemData.getMessage("upgrade-success").replace("{chance}", String.valueOf(chance));
                p.sendMessage(message);
                p.getInventory().addItem(output.build());
                if (ItemData.getCommand(id) != null) {
                    String command = ChatColor.translateAlternateColorCodes('&', ItemData.getCommand(id));
                    Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), command.replace("%player%", p.getName()));
                }
                Main.playSound(p, Sound.BLOCK_ANVIL_USE);
            } else {
                p.closeInventory();
                Main.saveLogFiles(p, 1, "강화 실패 (닉네임: " + p.getName() + ", 도구: " + id + ", 금액: " + cost + ", 요구 아이템:" + requireItem +  ")");
                p.sendMessage(ItemData.getMessage("upgrade-fail"));
                Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                return;
            }
        }
        ArrayList<Integer> slot = new ArrayList<>(Arrays.asList(new Integer[] {}));
        if (event.getRawSlot() > 53)
            return;
        for (int i = 0; i < 54; i++)
            slot.add(i);
        if (slot.get(event.getRawSlot()) != null) {
            if (event.getRawSlot() != 22) {
                event.setCancelled(true);
                return;
            }
        }


    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if (!event.getView().getTitle().contains("七七七七七七七七♿"))
            return;
        if (inv.getItem(22) == null)
            return;
        p.getInventory().addItem(inv.getItem(22));
    }


}
