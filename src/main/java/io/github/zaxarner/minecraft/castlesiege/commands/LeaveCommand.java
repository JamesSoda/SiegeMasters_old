package io.github.zaxarner.minecraft.castlesiege.commands;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by JamesCZ98 on 8/6/2019.
 */
public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CastleSiege.getPlugin().getGame(player).leaveGame(player);
            CastleSiege.getPlugin().getGame(player).removePlayer(player);
            Bukkit.getScheduler().runTaskLater(CastleSiege.getPlugin(), () -> {
                PlayerUtils.getLobbyInventory(player);
            }, 2L);

        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
        }

        return true;
    }
}
