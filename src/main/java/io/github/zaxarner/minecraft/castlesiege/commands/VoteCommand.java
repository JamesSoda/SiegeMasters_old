package io.github.zaxarner.minecraft.castlesiege.commands;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import io.github.zaxarner.minecraft.castlesiege.utils.DataFile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.logging.Level.INFO;

/**
 * Created by JamesCZ98 on 12/12/2019.
 */
public class VoteCommand implements CommandExecutor {


    private DataFile config = CastleSiege.getDataFile();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> votingSites = config.getConfig().getStringList("voting-sites");

        sender.sendMessage(ChatColor.DARK_AQUA + "Vote for us on: ");
        for(int i=0; i < votingSites.size(); i++) {
            String s = votingSites.get(i);
            if(s.split("\\|").length == 2) {
                String name = s.split("\\|")[0];
                String url = s.split("\\|")[1];

                sender.sendMessage(ChatColor.DARK_AQUA + "[" + (i + 1) + "] " + ChatColor.AQUA + name);
                sender.sendMessage(ChatColor.GOLD + url);

            }
        }

        return true;

    }

}
