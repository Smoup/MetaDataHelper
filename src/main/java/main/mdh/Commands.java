package main.mdh;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

import static org.bukkit.ChatColor.*;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Block block = MetaDataHelper.getLastInteractBlock().get(sender.getName());
        if (args.length == 0) {
            if (MetaDataHelper.getEnable().get(sender.getName()) == null || MetaDataHelper.getEnable().get(sender.getName()).equals(false)) {
                MetaDataHelper.getEnable().put(sender.getName(), true);
                sender.sendMessage(ChatColor.GRAY + "Вы включили " + ChatColor.YELLOW + "MetaDataHelper" + ChatColor.GRAY + " для себя");
            } else {
                MetaDataHelper.getEnable().put(sender.getName(), false);
                sender.sendMessage(ChatColor.GRAY + "Вы отключили " + ChatColor.YELLOW + "MetaDataHelper" + ChatColor.GRAY + " для себя");
            }
            return true;
        }
        if (MetaDataHelper.getEnable().get(sender.getName()) != null && MetaDataHelper.getEnable().get(sender.getName())) {
            if (block != null) {
                if (args[0].equals("get")) {
                    if (args.length > 1) {
                        String key = args[1];
                        List<MetadataValue> metadataList = block.getMetadata(key);
                        if (!metadataList.isEmpty()) {
                            for (MetadataValue mV : metadataList) {
                                sender.sendMessage(mV.asString());
                            }
                        } else {
                            sender.sendMessage(
                                    YELLOW + "MetaData " +
                                    GRAY + "под данным ключём отсутствует в выбранном блоке");
                        }
                    } else {
                        sender.sendMessage("/mdh get [key]");
                    }
                } else if (args[0].equals("set")) {
                    if (args.length > 1) {
                        String key = args[1];
                        if (!args[2].isEmpty()) {
                            if (!args[3].isEmpty()) {
                                String value = null;
                                for (int i = 3; i < args.length; i++) {
                                    value = value + args[i] + " ";
                                }
                                value = value.replace("null", "");
                                MetadataValue metadataValue = new FixedMetadataValue(MetaDataHelper.getInstance(), value);
                                block.setMetadata(key, metadataValue);
                                sender.sendMessage(
                                        GRAY + "Для блока " +
                                                YELLOW + block.getBlockData().getMaterial() +
                                                GRAY + " установлено значение " +
                                                YELLOW + value +
                                                GRAY + " под ключём " +
                                                YELLOW + key);
                            }
                        }
                    } else {
                        sender.sendMessage("/mdh set [key] [dataType] [value]");
                    }
                }
            } else {
                sender.sendMessage(GRAY + "Для выбора блока используй " + YELLOW + "ПКМ");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (MetaDataHelper.getEnable().get(sender.getName()) == null) {
            return null;
        }
        if (MetaDataHelper.getEnable().get(sender.getName())) {
            if (args.length == 1) {
                return Lists.newArrayList("get", "set");
            } else if (args.length == 2) {
                return Lists.newArrayList("key");
            } else if (args.length == 3) {
                if (args[0].equals("set")) {
                    return Lists.newArrayList("Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double", "String");
                }
            }
        }
        return null;
    }
}
