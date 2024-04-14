package com.flow.xupgrade.inventory;

import com.flow.xupgrade.Main;
import com.flow.xupgrade.database.ItemData;
import com.flow.xupgrade.hook.MMOItemBridger;
import com.flow.xupgrade.hook.MMOItemHook;
import com.flow.xupgrade.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.flow.xupgrade.Main.econ;

public class BukkitUpgradeInventory implements InventoryHolder, Listener {

    private Player player;
    private UUID uuid;
    private final Inventory inv;

    private static ItemStack airButton;
    private static ItemStack iconButton;
    private static ItemStack barrierButton;

    static {
        airButton = new ItemStack(Material.AIR);
        iconButton = new ItemStack(Material.COAL);
        barrierButton = new ItemStack(Material.COAL);
        ItemUtil.updateName(barrierButton, " ");
        ItemUtil.updateName(iconButton, " ");
        ItemUtil.updateModelData(barrierButton, 1);
        ItemUtil.updateModelData(iconButton, Main.instance.getConfig().getInt("item.upgrade.customModelData", 1));
    }

    public BukkitUpgradeInventory(Player player) {
        if (player != null) {
            this.player = player;
            this.uuid = player.getUniqueId();
        }
        this.inv = Bukkit.createInventory(this, 54, "");
        initializeItems();
    }

    public void initializeItems() {
        if (this.player == null) {
            return;
        }
        ItemStack tool = player.getItemInHand();
        for (int i = 0; i < 54; i++) {
            if (i != 20)
                inv.setItem(i, barrierButton);
        }
        inv.setItem(0, iconButton);
        inv.setItem(24, ItemUtil.setItem(Material.COAL, "§f", 1));
        inv.setItem(39, ItemUtil.setItem(Material.COAL, "§a강화", 1));
        inv.setItem(40, ItemUtil.setItem(Material.COAL, "§a강화", 1));
        inv.setItem(41, ItemUtil.setItem(Material.COAL, "§a강화", 1));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof BukkitUpgradeInventory))
            return;
        ItemStack clickedItem = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        Inventory clickedInv = e.getClickedInventory();
        if (clickedItem == null)
            return;
        if (clickedItem.getType().equals(Material.AIR))
            return;
        if (clickedItem.getItemMeta().getDisplayName().equals("§a강화")) {
            e.setCancelled(true);
            ItemStack item = inv.getItem(20);
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
                    if (!MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).isPresent()) {
                        p.sendMessage("§c재료가 올바르지 않습니다. 관리자에게 문의하세요.");
                        return;
                    }
                    ItemStack removeItem = MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build();
                    removeItem.setAmount(requireAmount);
                    if (!p.getInventory().containsAtLeast(MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build(), requireAmount)) {
                        p.sendMessage("§c재료가 부족합니다.");
                        Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                        return;
                    }
                    p.getInventory().removeItem(removeItem);
                }
            } else if (condition.equals("&&")) {
                cost = ItemData.getRequirementMoney(id);
                requireItem = ItemData.getItemRequire(id);
                int requireAmount = ItemData.getRequirementItemAmount(id);
                if (!MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).isPresent()) {
                    p.sendMessage("§c재료가 올바르지 않습니다. 관리자에게 문의하세요.");
                    return;
                }
                ItemStack removeItem = MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build();
                removeItem.setAmount(requireAmount);
                if (econ.getBalance(p) < cost) {
                    p.sendMessage(ItemData.getMessage("upgrade-nomoney"));
                    Main.playSound(p, Sound.BLOCK_ANVIL_PLACE);
                    return;
                }
                if (!p.getInventory().containsAtLeast(MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build(), requireAmount)) {
                    p.sendMessage("§c재료가 부족합니다.");
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
                inv.setItem(20, new ItemStack(Material.AIR, 1));
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
        if (e.getRawSlot() > 53)
            return;
        for (int i = 0; i < 54; i++)
            slot.add(i);
        if (slot.get(e.getRawSlot()) != null) {
            if (e.getRawSlot() != 20) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if (!(event.getInventory().getHolder() instanceof BukkitUpgradeInventory))
            return;
        if (inv.getItem(20) == null)
            return;
        p.getInventory().addItem(inv.getItem(20));
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public void openInventory(HumanEntity ent) {
        ent.openInventory(this.inv);
    }

}
