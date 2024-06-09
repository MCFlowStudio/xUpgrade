package com.flow.xupgrade.database;

import com.flow.xupgrade.hook.MMOItemBridger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static com.flow.xupgrade.Main.instance;

public class ItemData {

    public static String getMessage(String value) {
        return instance.getConfig().getString("message." + value);
    }
    public static Boolean hasItemData(String id) {
        if (instance.getConfig().getString("upgrade_list." + id) != null)
            return true;
        return false;
    }
    public static Integer getItemCost(String id) {
        return instance.getConfig().getInt("upgrade_list." + id + ".cost");
    }

    public static Boolean getItemRequireEnable(String id) {
        if (instance.getConfig().getString("upgrade_list." + id + ".requireitem.id") != null)
            return true;
        return false;
    }

    public static String getItemRequireID(String id) {
        return instance.getConfig().getString("upgrade_list." + id + ".requireitem.id");
    }

    public static MMOItemBridger getItemRequire(String id) {
        // 설정 파일에서 문자열 가져오기
        String requireupgrade_listtring = instance.getConfig().getString("upgrade_list." + id + ".requirement.item_id");

        // ":"를 기준으로 문자열 분할
        if (requireupgrade_listtring != null && requireupgrade_listtring.contains("@")) {
            String[] parts = requireupgrade_listtring.split("@");
            if (parts.length == 2) {
                // parts[0]은 타입, parts[1]은 아이템 ID
                return new MMOItemBridger(parts[0], parts[1]);
            }
        }

        // 적절한 값이 설정되어 있지 않은 경우 null 반환
        return null;
    }

    public static Integer getItemRequireAmount(String id) {
        return instance.getConfig().getInt("upgrade_list." + id + ".requireitem.amount");
    }

    public static Integer getItemChance(String id) {
        return instance.getConfig().getInt("upgrade_list." + id + ".chance");
    }

    public static MMOItemBridger getPreviousItem(String outputId) {
        FileConfiguration config = instance.getConfig();
        // "upgrade_list" 섹션 내의 모든 항목을 순회
        ConfigurationSection upgrade_listSection = config.getConfigurationSection("upgrade_list");
        if (upgrade_listSection != null) {
            for (String key : upgrade_listSection.getKeys(false)) {
                String outputPath = "upgrade_list." + key + ".output";
                String outputupgrade_listtring = config.getString(outputPath);
                // 찾고자 하는 outputId와 일치하는지 확인
                if (outputupgrade_listtring != null && outputupgrade_listtring.equals(outputId)) {
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
        String outputupgrade_listtring = instance.getConfig().getString("upgrade_list." + id + ".output");

        // ":"를 기준으로 문자열 분할
        if (outputupgrade_listtring != null && outputupgrade_listtring.contains("@")) {
            String[] parts = outputupgrade_listtring.split("@");
            if (parts.length == 2) {
                return new MMOItemBridger(parts[0], parts[1]);
            }
        }

        // 적절한 값이 설정되어 있지 않은 경우 null 반환
        return null;
    }

    public static String getCommand(String id) {
        return instance.getConfig().getString("upgrade_list." + id + ".command");
    }

    //
    public static String getCondition(String id) {
        return instance.getConfig().getString("upgrade_list." + id + ".condition", "==");
    }

    public static String getType(String id) {
        return instance.getConfig().getString("upgrade_list." + id + ".type");
    }

    public static Integer getRequirementMoney(String id) {
        return instance.getConfig().getInt("upgrade_list." + id + ".requirement.money");
    }

    public static String getRequirementItemID(String id) {
        return instance.getConfig().getString("upgrade_list." + id + ".requirement.item_id");
    }

    public static Integer getRequirementItemAmount(String id) {
        return instance.getConfig().getInt("upgrade_list." + id + ".requirement.item_amount");
    }

}
