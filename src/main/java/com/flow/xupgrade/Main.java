package com.flow.xupgrade;

import com.flow.xupgrade.command.Basic;
import com.flow.xupgrade.command.Upgrade;
import com.flow.xupgrade.database.ItemData;
import com.flow.xupgrade.database.ItemStorage;
import com.flow.xupgrade.hook.MMOItemBridger;
import com.flow.xupgrade.hook.MMOItemHook;
import com.flow.xupgrade.inventory.BukkitUpgradeInventory;
import com.flow.xupgrade.listener.ClickEvent;
import com.flow.xupgrade.util.ItemUtil;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static com.flow.xupgrade.command.Basic.setItem;
import static com.flow.xupgrade.command.Basic.updateLore;
import static com.flow.xupgrade.database.ItemData.hasItemData;

public final class Main extends JavaPlugin {

    public static YamlConfiguration upgradeItems;

    public static Main instance;

    public static Economy econ = null;

    public static Logger logger = Logger.getLogger("xUpgrade");

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 2.0F, 1.0F);
    }

    public static void createFiles() throws IOException {
        File file = new File(instance.getDataFolder(), "upgrade-fail.log");
        if (!file.exists()) {
            file.createNewFile();
        }
        File file2 = new File(instance.getDataFolder(), "upgrade-success.log");
        if (!file2.exists()) {
            file2.createNewFile();
        }

    }

    public static void saveLogFiles(Player player, Integer mode, String text) {
        try {
            createFiles();
            Timestamp date = new Timestamp(System.currentTimeMillis());
            if (mode.equals(1)) {
                File userFile = new File(instance.getDataFolder(), "upgrade-fail.log");
                if (!userFile.exists())
                    userFile.createNewFile();
                BufferedWriter bufWr = new BufferedWriter(new FileWriter(userFile, true));
                if (userFile.isFile()) {
                    bufWr.write("[" + date + "] " + text + "\n");
                    bufWr.flush();
                    bufWr.close();
                }
            } else if (mode.equals(2)) {
                File userFile = new File(instance.getDataFolder(), "upgrade-success.log");
                if (!userFile.exists())
                    userFile.createNewFile();
                BufferedWriter bufWr = new BufferedWriter(new FileWriter(userFile, true));
                if (userFile.isFile()) {
                    bufWr.write("[" + date + "] " + text + "\n");
                    bufWr.flush();
                    bufWr.close();
                }
            }
        } catch (Exception exception) {}
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("장비강화").setExecutor((CommandExecutor)new Basic());
        getCommand("upgrade").setExecutor((CommandExecutor)new Upgrade());
        getServer().getPluginManager().registerEvents((Listener) new BukkitUpgradeInventory(null), (Plugin)this);
        setUpEconomy();
        repeating();
        try {
            createFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean setUpEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = (Economy)rsp.getProvider();
        return (econ != null);
    }

    public void repeating() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) Main.instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof BukkitUpgradeInventory))
                    continue;
                Inventory inv = player.getOpenInventory().getTopInventory();
                ItemStack item = inv.getItem(20);
                if (!MMOItemHook.findMMOItem(item).isPresent()) {
                    if (inv.getItem(20) != null) {
                        inv.setItem(24, setItem(Material.BARRIER, "§f", 10003));
                    } else {
                        inv.setItem(24, setItem(Material.COAL, "§f", 1));
                    }
                    inv.setItem(39, ItemUtil.setItem(Material.COAL, "§a강화", 1));
                    inv.setItem(40, ItemUtil.setItem(Material.COAL, "§a강화", 1));
                    inv.setItem(41, ItemUtil.setItem(Material.COAL, "§a강화", 1));
                    continue;
                }
                MMOItem requirementItem = MMOItemHook.findMMOItem(item).get();
                String id = requirementItem.getType().getId() + "@" + requirementItem.getId();
                if (id == null || hasItemData(id) == false) {
                    if (inv.getItem(20) != null) {
                        inv.setItem(24, setItem(Material.BARRIER, "§f", 10003));
                    } else {
                        inv.setItem(24, setItem(Material.COAL, "§f", 1));
                    }
                    inv.setItem(39, ItemUtil.setItem(Material.COAL, "§a강화", 1));
                    inv.setItem(40, ItemUtil.setItem(Material.COAL, "§a강화", 1));
                    inv.setItem(41, ItemUtil.setItem(Material.COAL, "§a강화", 1));
                    continue;
                }

                int chance = ItemData.getItemChance(id);
                MMOItemBridger output = ItemData.getOutputItem(requirementItem.getType().getId() + "@" + requirementItem.getId());
                inv.setItem(24, MMOItemHook.findMMOItem(output.getType(), output.getId()).get().newBuilder().build());

                ItemStack start = setItem(Material.COAL, "§a강화", 1);
                ArrayList<String> loreList = new ArrayList<>();

                String condition = getConfig().getString("upgrade_list." + id + ".condition");

                if (condition.equals("==") && "money".equals(getConfig().getString("upgrade_list." + id + ".type"))) {
                    int cost = getConfig().getInt("upgrade_list." + id + ".requirement.money");
                    loreList.add("§7강화 비용 : §e" + cost + "원");
                }

                if (condition.equals("==") && "item".equals(getConfig().getString("upgrade_list." + id + ".type"))) {
                    MMOItemBridger requireItem = ItemData.getItemRequire(id);
                    int requireItemAmount = getConfig().getInt("upgrade_list." + id + ".requirement.item_amount");
                    String requireName = MMOItemHook.findMMOItem(requireItem.getType(), requireItem.getId()).get().newBuilder().build().getItemMeta().getDisplayName();
                    loreList.add("§7필요 아이템 : §f" + requireName + " §7(" + requireItemAmount + "§7개)");
                }

                if (condition.equals("&&")) {
                    int cost = getConfig().getInt("upgrade_list." + id + ".requirement.money");
                    loreList.add("§7강화 비용 : §e" + cost + "원");

                    MMOItemBridger requireItem = ItemData.getItemRequire(id);
                    int requireItemAmount = getConfig().getInt("upgrade_list." + id + ".requirement.item_amount");
                    ItemMeta itemMeta = requireItem.build().getItemMeta();
                    String requireName;
                    if (itemMeta != null && itemMeta.hasDisplayName()) {
                        requireName = itemMeta.getDisplayName();
                    } else {
                        getLogger().info("아이템 오류 : " + id + " | ID : " + requireItem.getId() + " TYPE : " + requireItem.getType().getId());
                        requireName = requireItem.build().getType().name();
                    }
                    loreList.add("§7필요 아이템 : §f" + requireName + " §7(" + requireItemAmount + "§7개)");
                }

                loreList.add("§7성공 확률 : §f" + chance + "%");

                updateLore(start, loreList);

                inv.setItem(39, start);
                inv.setItem(40, start);
                inv.setItem(41, start);
            }
        }, 0L, 5L);
    }


    public static Map<String, Object> getRequirements(String id) {
        if (hasItemData(id)) {
            String condition = instance.getConfig().getString("upgrade_list." + id + ".condition");
            String type = instance.getConfig().getString("upgrade_list." + id + ".type");
            Map<String, Object> requirements = instance.getConfig().getConfigurationSection("upgrade_list." + id + ".requirement").getValues(false);
            requirements.put("condition", condition);
            requirements.put("type", type);
            return requirements;
        }
        return null;
    }


    @Override
    public void onDisable() {
        //saveItemData();
    }
}
