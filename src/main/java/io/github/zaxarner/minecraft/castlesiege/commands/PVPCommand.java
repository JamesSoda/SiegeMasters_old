package io.github.zaxarner.minecraft.castlesiege.commands;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created on 4/19/2020.
 *
 * @author Zaxarner
 */
public class PVPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(player.getWorld() != CastleSiege.getCreativeSpawn().getWorld()) {
                player.sendMessage(ChatColor.RED + "You are already in the PVP World!");
            } else {
                player.teleport(CastleSiege.getSpawn());
                player.setGameMode(GameMode.SURVIVAL);
                PlayerUtils.getLobbyInventory(player);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "Type '/creative' to get back to the Creative world!");
                player.sendMessage(" ");
            }

            return true;

        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
        }

        return true;
    }
}
