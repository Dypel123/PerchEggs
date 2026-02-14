package me.perch.util;

import me.perch.Eggs;
import me.perch.manager.ConfigManager;
import me.perch.manager.EggManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EggCommand implements TabExecutor {

    private final Eggs plugin;

    public EggCommand(Eggs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ConfigManager cm = plugin.getConfigManager();
        EggManager em = plugin.getEggManager();

        if (!sender.hasPermission("percheggs.admin")) {
            sender.sendMessage(cm.getMessage("messages.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(cm.getMessage("messages.invalid-args"));
            return true;
        }

        String type = args[0].equalsIgnoreCase("give") ? args[1].toLowerCase() : args[0].toLowerCase();

        if (!args[0].equalsIgnoreCase("give")) return true;
        type = args[1].toLowerCase();

        if (!type.equals("singleuse") && !type.equals("multipleuse")) {
            sender.sendMessage(cm.getMessage("messages.invalid-args"));
            return true;
        }

        int amountOfItems = 1;
        if (args.length >= 3) {
            try {
                amountOfItems = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(cm.parse("<red>Invalid amount."));
                return true;
            }
        }

        int usesPerItem = 1;
        Player targetPlayer = null;

        if (type.equals("singleuse")) {
            if (args.length >= 4) {
                targetPlayer = Bukkit.getPlayer(args[3]);
            }
        } else {
            usesPerItem = 5;
            if (args.length >= 4) {
                try {
                    usesPerItem = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(cm.parse("<red>Invalid uses number."));
                    return true;
                }
            }
            if (args.length >= 5) {
                targetPlayer = Bukkit.getPlayer(args[4]);
            }
        }

        if (targetPlayer == null) {
            if (sender instanceof Player p) {
                targetPlayer = p;
            } else {
                sender.sendMessage(cm.parse("<red>Console must specify a player."));
                return true;
            }
        }

        ItemStack eggItem = em.createPerchEgg(usesPerItem);
        eggItem.setAmount(amountOfItems);

        if (targetPlayer.getInventory().firstEmpty() != -1) {
            targetPlayer.getInventory().addItem(eggItem);
        } else {
            targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), eggItem);
        }

        String msg = plugin.getConfig().getString("messages.give-success", "Gave egg.")
                .replace("%amount%", String.valueOf(amountOfItems))
                .replace("%type%", type + (type.equals("multipleuse") ? " (" + usesPerItem + " uses)" : ""))
                .replace("%player%", targetPlayer.getName());
        sender.sendMessage(cm.parse(msg));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("give");
            return completions;
        }

        if (!args[0].equalsIgnoreCase("give")) return null;

        if (args.length == 2) {
            completions.add("singleuse");
            completions.add("multipleuse");
        }
        else if (args.length == 3) {
            completions.add("1");
            completions.add("16");
            completions.add("64");
        }
        else {
            String type = args[1].toLowerCase();
            if (type.equals("singleuse")) {
                if (args.length == 4) return null;
            } else if (type.equals("multipleuse")) {
                if (args.length == 4) {
                    completions.add("5");
                    completions.add("10");
                }
                else if (args.length == 5) {
                    return null;
                }
            }
        }

        return completions;
    }
}