package io.github.zaxarner.minecraft.castlesiege.commands.staff;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 4/18/2020.
 *
 * @author James Coppock
 */
public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            player.teleport(CastleSiege.getSpawn());
            PlayerUtils.getLobbyInventory(player);
            return true;
        }

        sender.sendMessage("You must be a player!");
        return true;
    }
}
