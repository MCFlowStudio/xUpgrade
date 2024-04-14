package com.flow.xupgrade.command;

import com.flow.xupgrade.Main;
import com.flow.xupgrade.hook.MMOItemHook;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Upgrade implements CommandExecutor {

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.isOp() &&
                    sender instanceof org.bukkit.entity.Player) {
                sender.sendMessage("§c권한이 부족합니다.");
                return false;
            }
            sender.sendMessage("/upgrade reload");
        } else {
            Player player = (Player)sender;
            String lowerCase;
            switch (lowerCase = args[0].toLowerCase()) {
                case "reload":
                    if (!sender.isOp() &&
                            sender instanceof org.bukkit.entity.Player) {
                        sender.sendMessage("§c권한이 부족합니다.");
                        break;
                    }
                    Main.instance.reloadConfig();
                    sender.sendMessage("loaded");
                    break;
                case "item":
                    ItemStack handItem = player.getItemInHand();
                    if (handItem == null)
                        return false;
                    if (!MMOItemHook.findMMOItem(handItem).isPresent())
                        return true;

                    sender.sendMessage("TYPE : " + MMOItemHook.findMMOItem(handItem).get().getType().getId());
                    sender.sendMessage("ITEM ID : " + MMOItemHook.findMMOItem(handItem).get().getId());
                    break;
            }
        }
        return true;
    }

}
