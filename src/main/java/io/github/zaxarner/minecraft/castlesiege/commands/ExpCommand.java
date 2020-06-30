package io.github.zaxarner.minecraft.castlesiege.commands;

import org.jetbrains.annotations.NotNull;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import io.github.zaxarner.minecraft.castlesiege.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by JamesCZ98 on 12/12/2019.
 */
public class ExpCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            PlayerProfile profile = PlayerProfile.getProfile(player);



            player.sendMessage(ChatColor.AQUA + "You are Level " + ChatColor.GOLD + profile.getLevel());
            player.sendMessage(ChatColor.AQUA + "You have " + ChatColor.GOLD + profile.getExp() + ChatColor.AQUA + "/" +
                    ChatColor.GOLD + PlayerProfile.getRequiredExpForLevel(profile.getLevel() + 1) + ChatColor.AQUA + " Exp.");

            if(profile.getExpBoostDuration() > 0) {
                player.sendMessage(ChatColor.AQUA + "You have an Exp. Boost for the next " + ChatColor.DARK_AQUA + StringUtils.formatTime(profile.getExpBoostDuration()));
            } else {
                player.sendMessage(ChatColor.AQUA + "You do not have an Exp. Boost! " + ChatColor.DARK_AQUA + "Vote " + ChatColor.AQUA + "to receive one!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
        }

        return true;
    }


}
