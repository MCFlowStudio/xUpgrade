package com.flow.xupgrade.database;

import com.flow.xupgrade.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemStorage {

    private static Map<String, ItemStack> storage = new HashMap<>();
    private static File itemDataFile;
    private static FileConfiguration itemDataConfig;

    public static void createItemDataFile() {
        itemDataFile = new File(Main.instance.getDataFolder(), "ItemData.yml");
        if (!itemDataFile.exists()) {
            itemDataFile.getParentFile().mkdirs();
            Main.instance.saveResource("ItemData.yml", false);
        }
        itemDataConfig = YamlConfiguration.loadConfiguration(itemDataFile);
    }

    // 파일에서 storage 변수로 데이터 로드
    public static void loadItemData() {
        storage.clear(); // 기존 데이터를 클리어
        itemDataConfig.getKeys(false).forEach(key -> {
            ItemStack item = itemDataConfig.getItemStack(key);
            if (item != null) {
                storage.put(key, item);
            }
        });
    }

    // 아이템 저장
    public static void saveItem(String ID, ItemStack itemStack) {
        storage.put(ID, itemStack);
    }

    // 아이템 로드
    public static Optional<ItemStack> loadItem(String ID) {
        return Optional.ofNullable(storage.get(ID));
    }

    // 아이템 찾기
    public static Optional<String> findItem(ItemStack itemStack) {
        return storage.entrySet().stream()
                .filter(entry -> {
                    ItemStack storedItem = entry.getValue();
                    if (storedItem.getType() != itemStack.getType()) {
                        return false; // 아이템 종류가 다르면 false
                    }
                    ItemMeta storedMeta = storedItem.getItemMeta();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (storedMeta != null && itemMeta != null) {
                        return storedMeta.equals(itemMeta); // 메타데이터가 있는 경우, 메타데이터 비교
                    }
                    return storedMeta == null && itemMeta == null; // 둘 다 메타데이터가 없는 경우 true
                })
                .map(Map.Entry::getKey)
                .findFirst();
    }
    // 아이템 삭제
    public static void removeItem(String ID) {
        storage.remove(ID);
    }

}
