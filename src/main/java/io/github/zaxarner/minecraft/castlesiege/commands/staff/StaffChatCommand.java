package io.github.zaxarner.minecraft.castlesiege.commands.staff;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.game.DeathmatchGame;
import io.github.zaxarner.minecraft.castlesiege.game.map.MapInstantiationException;
import io.github.zaxarner.minecraft.castlesiege.game.map.MonasteryMap;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesCZ98 on 11/30/2019.
 */
public class StaffChatCommand  implements CommandExecutor {

    public static List<Player> staffChatters = new ArrayList<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(!sender.hasPermission("castlesiege.staff"))
            return false;

        if(args.length > 0) {
            StringBuilder message = new StringBuilder();
            for (String s : args) {
                message.append(s).append(" ");
            }
            staffChatBroadcast(sender.getName() + " : " + message.toString());
            return true;
        } else {

            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(staffChatters.contains(player)) {
                    staffChatters.remove(player);
                    player.sendMessage(ChatColor.GOLD + "[StaffChat] Toggled " + ChatColor.RED + "off");
                    return true;
                } else {
                    staffChatters.add(player);
                    player.sendMessage(ChatColor.GOLD + "[StaffChat] Toggled " + ChatColor.GREEN + "on");
                    return true;
                }

            } else {
                sender.sendMessage(ChatColor.RED + "You must be a Player to toggle StaffChat!");
                return true;
            }
        }
    }

    public static void staffChatBroadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("castlesiege.staff")) {
                player.sendMessage(ChatColor.GOLD + "[StaffChat] " + ChatColor.YELLOW + message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[StaffChat] " + ChatColor.YELLOW + message);
    }
}
