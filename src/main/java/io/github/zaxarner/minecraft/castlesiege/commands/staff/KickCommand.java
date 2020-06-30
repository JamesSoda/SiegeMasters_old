package io.github.zaxarner.minecraft.castlesiege.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by JamesCZ98 on 12/10/2019.
 */
public class KickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if(args.length > 0) {

            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);

            if(target == null) {
                sender.sendMessage(ChatColor.RED + "That is not an online player!");
                return true;
            }

            if(args.length > 1) {

                StringBuilder message = new StringBuilder();
                for(int i=1; i < args.length; i++) {
                    message.append(args[i]).append(" ");
                }

                target.kickPlayer(ChatColor.GRAY + message.toString());
            } else {
                target.kickPlayer(ChatColor.GRAY + "You have been kicked!");
            }

            return true;

        }

        return false;
    }
}
