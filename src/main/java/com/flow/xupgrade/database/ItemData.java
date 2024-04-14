package com.flow.xupgrade.database;

import com.flow.xupgrade.hook.MMOItemBridger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import static com.flow.xupgrade.Main.instance;

public class ItemData {

    public static String getMessage(String value) {
        return instance.getConfig().getString("message." + value);
    }
    public static Boolean hasItemData(String id) {
        if (instance.getConfig().getString("items." + id) != null)
            return true;
        return false;
    }
    public static Integer getItemCost(String id) {
        return instance.getConfig().getInt("items." + id + ".cost");
    }

    public static Boolean getItemRequireEnable(String id) {
        if (instance.getConfig().getString("items." + id + ".requireitem.id") != null)
            return true;
        return false;
    }

    public static String getItemRequireID(String id) {
        return instance.getConfig().getString("items." + id + ".requireitem.id");
    }

    public static MMOItemBridger getItemRequire(String id) {
        // 설정 파일에서 문자열 가져오기
        String requireItemString = instance.getConfig().getString("items." + id + ".requirement.item_id");

        // ":"를 기준으로 문자열 분할
        if (requireItemString != null && requireItemString.contains("@")) {
            String[] parts = requireItemString.split("@");
            if (parts.length == 2) {
                // parts[0]은 타입, parts[1]은 아이템 ID
                return new MMOItemBridger(parts[0], parts[1]);
            }
        }

        // 적절한 값이 설정되어 있지 않은 경우 null 반환
        return null;
    }

    public static Integer getItemRequireAmount(String id) {
        return instance.getConfig().getInt("items." + id + ".requireitem.amount");
    }

    public static Integer getItemChance(String id) {
        return instance.getConfig().getInt("items." + id + ".chance");
    }

    public static MMOItemBridger getPreviousItem(String outputId) {
        FileConfiguration config = instance.getConfig();
        // "items" 섹션 내의 모든 항목을 순회
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                String outputPath = "items." + key + ".output";
                String outputItemString = config.getString(outputPath);
                // 찾고자 하는 outputId와 일치하는지 확인
                if (outputItemString != null && outputItemString.equals(outputId)) {
                    // ":" 대신 "@"를 사용하므로 분할 방식 조정 필요
                    String[] parts = key.split("@");
                    if (parts.length == 2) {
                        return new MMOItemBridger(parts[0], parts[1]);
                    }
                }
            }
        }

        // 일치하는 항목이 없는 경우
        return null;
    }

    public static MMOItemBridger getOutputItem(String id) {
        // 설정 파일에서 문자열 가져오기
        String outputItemString = instance.getConfig().getString("items." + id + ".output");

        // ":"를 기준으로 문자열 분할
        if (outputItemString != null && outputItemString.contains("@")) {
            String[] parts = outputItemString.split("@");
            if (parts.length == 2) {
                return new MMOItemBridger(parts[0], parts[1]);
            }
        }

        // 적절한 값이 설정되어 있지 않은 경우 null 반환
        return null;
    }

    public static String getCommand(String id) {
        return instance.getConfig().getString("items." + id + ".command");
    }

    //
    public static String getCondition(String id) {
        return instance.getConfig().getString("items." + id + ".condition", "==");
    }

    public static String getType(String id) {
        return instance.getConfig().getString("items." + id + ".type");
    }

    public static Integer getRequirementMoney(String id) {
        return instance.getConfig().getInt("items." + id + ".requirement.money");
    }

    public static String getRequirementItemID(String id) {
        return instance.getConfig().getString("items." + id + ".requirement.item_id");
    }

    public static Integer getRequirementItemAmount(String id) {
        return instance.getConfig().getInt("items." + id + ".requirement.item_amount");
    }

}
