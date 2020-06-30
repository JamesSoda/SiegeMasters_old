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
 * Created on 4/18/2020.
 *
 * @author Zaxarner
 */
public class CreativeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(player.getWorld() != CastleSiege.getSpawn().getWorld() && player.getWorld() != CastleSiege.getCreativeSpawn().getWorld()) {
                player.sendMessage(ChatColor.RED + "You must be in the lobby to join Creative!");
            } else if(player.getWorld() == CastleSiege.getCreativeSpawn().getWorld()) {
                player.sendMessage(ChatColor.RED + "You are already in the Creative World!");
            } else {
                player.getInventory().clear();
                player.teleport(CastleSiege.getCreativeSpawn());
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "Type '/pvp' to get back to the PVP world!");
                player.sendMessage(" ");
            }

            return true;

        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
        }

        return true;
    }
}