package io.github.zaxarner.minecraft.castlesiege.commands;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.player.PlayerProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by JamesCZ98 on 7/31/2019.
 */
public class LoadoutCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {

            Player player = (Player) sender;
            PlayerProfile playerProfile = PlayerProfile.getProfile(player);

            if(playerProfile == null) {
                player.sendMessage(ChatColor.RED + "You do not have a PlayerProfile registered. Try re-logging.");
                return true;
            }

            if(args.length == 0) {
                CastleSiege.loadoutMenu.openInventory(player);
                return true;
            }


            if(args[0].equalsIgnoreCase("set")) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Invalid Args! /loadout set [loadout-number]");
                    return true;
                }

                int loadoutNumber;

                try {
                    loadoutNumber = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }

                if(playerProfile.setCurrentLoadout(loadoutNumber)) {
                    player.sendMessage(ChatColor.GREEN + "Equipped Loadout #" + loadoutNumber);
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to equip Loadout# " + loadoutNumber);
                }
            }

        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player!");
        }

        return true;
    }
}